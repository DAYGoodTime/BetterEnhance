package com.day.betterexperience.common.machine.hatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.OutputBusType;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.IOutputBusTransaction;
import gregtech.api.interfaces.IOutputTransaction;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputBusME;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;
import gregtech.common.tileentities.machines.outputme.util.AECacheCounter;

/** An interface view only: never registered, never attached to a BaseMetaTileEntity. */
public final class MEOutputItemView implements IOutputBus, MTEHatchOutputMEBase.Environment<IAEItemStack> {

    private final MEOutputAssembly owner;
    private final MTEHatchOutputMEBase<IAEItemStack> provider;

    MEOutputItemView(MEOutputAssembly owner) {
        this.owner = owner;
        provider = new MTEHatchOutputMEBase<IAEItemStack>(this) {

            @Override
            public AENetworkProxy getProxy() {
                return owner.getProxy();
            }

            @Override
            public void updateValidGridProxySides() {
                // Connection geometry belongs to the real hatch, not the independent item settings.
                owner.getProvider()
                    .updateValidGridProxySides();
            }
        };
    }

    @Override
    public boolean isFiltered() {
        return provider.isFiltered();
    }

    @Override
    public boolean isFilteredToItem(GTUtility.ItemId id) {
        return provider.canStore(AEItemStack.create(id.getItemStack()));
    }

    @Override
    public OutputBusType getBusType() {
        if (provider.getCacheMode())
            return isFiltered() ? OutputBusType.MECacheFiltered : OutputBusType.MECacheUnfiltered;
        return isFiltered() ? OutputBusType.MEFiltered : OutputBusType.MEUnfiltered;
    }

    @Override
    public boolean storePartial(ItemStack stack, boolean simulate) {
        IAEItemStack input = AEItemStack.create(stack);
        if (input == null) return true;
        provider.storePartial(input, simulate);
        stack.stackSize = (int) input.getStackSize();
        return stack.stackSize == 0;
    }

    @Override
    public IOutputBusTransaction createTransaction() {
        return new ItemTransaction();
    }

    /** Uses GT's native transaction interfaces, counter and ME provider, not a second output-bus MTE. */
    private final class ItemTransaction
        implements IOutputBusTransaction, IOutputTransaction.IRecipeCheckAware, IOutputTransaction.IProtectOutputAware {

        private final AECacheCounter<GTUtility.ItemId> pending = new AECacheCounter<>();
        private final long availableSpace = provider.getPhysicalSpace();
        private boolean active = true;
        private boolean recipeCheck;
        private boolean protectOutput = true;
        private boolean dynamicCapacity;
        private boolean allowAnyInput;
        private IMEInventoryHandler<IAEItemStack> cellSnapshot;
        private boolean pendingCacheFits = true;

        @Override
        public void setRecipeCheck(boolean checking) {
            recipeCheck = checking;
            if (checking && provider.shouldCheckCell() && cellSnapshot == null) {
                // Reserve against a copy, including not-yet-flushed outputs. Never flush or mutate the real cell
                // during recipe simulation; the snapshot's save callback must not mark the real tile dirty either.
                cellSnapshot = AEApi.instance()
                    .registries()
                    .cell()
                    .getCellInventory(getCellStack().copy(), inventory -> {}, getChannel());
                if (cellSnapshot != null) {
                    for (IAEItemStack cached : provider.getCacheList()) {
                        IAEItemStack rejected = cellSnapshot
                            .injectItems(cached.copy(), Actionable.MODULATE, getActionSource());
                        if (rejected != null && rejected.getStackSize() > 0) pendingCacheFits = false;
                    }
                } else {
                    pendingCacheFits = false;
                }
            }
            updateFlags();
        }

        @Override
        public void setProtectOutput(boolean protect) {
            protectOutput = protect;
            updateFlags();
        }

        private void updateFlags() {
            dynamicCapacity = recipeCheck && protectOutput
                && provider.getCheckMode()
                && (!provider.getCacheMode() || !provider.isDistribution())
                && !provider.canVoidOverflow();
            allowAnyInput = !provider.getCheckMode() && availableSpace > 0;
            if (!recipeCheck) allowAnyInput |= provider.getLastInputTick() == provider.getTickCounter();
        }

        @Override
        public boolean needsTotalParallelData() {
            return dynamicCapacity;
        }

        @Override
        public IOutputBus getBus() {
            return MEOutputItemView.this;
        }

        @Override
        public boolean hasAvailableSpace() {
            if (recipeCheck && provider.shouldCheckCell()) return pendingCacheFits;
            return allowAnyInput || pending.getTotal() < availableSpace;
        }

        @Override
        public boolean storePartial(GTUtility.ItemId id, @NotNull ItemStack stack, long totalPerParallel,
            long perParallel) {
            if (!active) throw new IllegalStateException("Output transaction already committed");
            if (!isFilteredTo(id) || !hasAvailableSpace()) return false;
            long amount = stack.stackSize;
            if (dynamicCapacity) {
                long space = provider.shouldCheckCell()
                    ? Math.max(0, provider.getCellAvailableSpace() - provider.getCachedAmount())
                    : availableSpace;
                long remaining = Math.max(0, space - pending.getTotal());
                if (totalPerParallel > 0 && perParallel > 0) {
                    long parallels = Math.max(1, Math.min(Integer.MAX_VALUE, space / totalPerParallel));
                    long weighted = perParallel > Long.MAX_VALUE / parallels ? Long.MAX_VALUE : parallels * perParallel;
                    amount = Math.min(amount, weighted);
                }
                amount = Math.min(amount, remaining);
            }
            if (amount <= 0) return false;
            if (recipeCheck && provider.shouldCheckCell()) {
                IAEItemStack input = AEItemStack.create(stack)
                    .setStackSize(amount);
                IAEItemStack rejected = cellSnapshot.injectItems(input, Actionable.MODULATE, getActionSource());
                amount -= rejected == null ? 0 : rejected.getStackSize();
            }
            if (amount <= 0) return false;
            pending.insert(id, amount);
            stack.stackSize -= (int) amount;
            return true;
        }

        @Override
        public void complete(GTUtility.ItemId id) {}

        @Override
        public void commit() {
            if (!active) throw new IllegalStateException("Output transaction already committed");
            active = false;
            // Recipe-check transactions must never produce real outputs, even if a caller commits one.
            if (recipeCheck || pending.getTotal() == 0) return;
            pending.iterateAll(
                (id, amount) -> provider.addToCache(
                    AEItemStack.create(id.getItemStack())
                        .setStackSize(amount)));
            provider.updateLastInputTick();
            owner.markDirty();
        }
    }

    @Override
    public IGregTechTileEntity getBaseMetaTileEntity() {
        return owner.getBaseMetaTileEntity();
    }

    @Override
    public IGridProxyable getIGridProxyable() {
        return owner;
    }

    @Override
    public StorageChannel getChannel() {
        return StorageChannel.ITEMS;
    }

    @Override
    public ItemStack getCellStack() {
        return owner.getItemCell();
    }

    @Override
    public ISaveProvider getISaveProvider() {
        return owner;
    }

    @Override
    public BaseActionSource getActionSource() {
        return owner.getActionSource();
    }

    @Override
    public EntityPlayer getLastClickedPlayer() {
        return owner.getLastClickedPlayer();
    }

    @Override
    public IMEInventory<IAEItemStack> getNetworkInvtory() throws GridAccessException {
        return owner.getProxy()
            .getStorage()
            .getItemInventory();
    }

    @Override
    public NBTTagCompound saveStackToNBT(IAEItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("itemStack", GTUtility.saveItem(stack.getItemStack()));
        tag.setLong("size", stack.getStackSize());
        return tag;
    }

    @Override
    public IAEItemStack loadStackFromNBT(NBTTagCompound tag) {
        ItemStack stack = GTUtility.loadItem(tag.getCompoundTag("itemStack"));
        return stack == null ? null
            : AEItemStack.create(stack)
                .setStackSize(tag.getLong("size"));
    }

    @Override
    public byte getColor() {
        return owner.getColor();
    }

    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return owner.getCopiedDataIdentifier(player) + ".items";
    }

    @Override
    public ItemStack getVisual() {
        return owner.getVisual();
    }

    @Override
    public void dispatchMarkDirty() {
        owner.markDirty();
    }

    @Override
    public void notifyOutputSpaceChanged() {
        owner.notifyOutputSpaceChanged();
    }

    @Override
    public MTEHatchOutputMEBase<IAEItemStack> getProvider() {
        return provider;
    }

    @Override
    public String getEnableKey() {
        return "GT5U.hatch.item.filter.enable";
    }

    @Override
    public String getDisableKey() {
        return "GT5U.hatch.item.filter.disable";
    }

    @Override
    public long getDefaultCapacity() {
        return MTEHatchOutputBusME.DEFAULT_CAPACITY;
    }
}
