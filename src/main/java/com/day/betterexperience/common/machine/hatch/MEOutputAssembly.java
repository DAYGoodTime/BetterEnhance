package com.day.betterexperience.common.machine.hatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.day.betterexperience.client.gui.MEOutputAssemblyGui;
import com.glodblock.github.common.item.ItemFluidVoidStorageCell;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;

import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.items.storage.ItemBasicStorageCell;
import appeng.items.storage.ItemVoidStorageCell;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTUtility;
import gregtech.api.util.GTWaila;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputME;
import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@IMetaTileEntity.SkipGenerateDescription
public class MEOutputAssembly extends MTEHatchOutputME {

    private static final String ITEM_OUTPUT_TAG = "betterexperienceItemOutput";
    private static final String ITEM_CELL_TAG = "betterexperienceItemCell";
    private static final String ITEM_WAILA_TAG = "betterexperienceItemWaila";
    private static final String COPY_ID = "betterexperience.me_output_assembly";

    // The native fluid hatch has one final inventory slot. Keep the item cell in a separate handler, not another MTE.
    private final ItemStackHandler itemCellInventory = new ItemStackHandler(1) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isItemCell(stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (itemOutput != null) itemOutput.getProvider()
                .onContentsChanged(0);
            markDirty();
        }
    };

    private final MEOutputItemView itemOutput = new MEOutputItemView(this);

    public MEOutputAssembly(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public MEOutputAssembly(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new MEOutputAssembly(mName, mTier, mDescriptionArray, mTextures);
    }

    public MEOutputItemView getItemOutput() {
        return itemOutput;
    }

    public ItemStackHandler getItemCellInventory() {
        return itemCellInventory;
    }

    public ItemStack getItemCell() {
        return itemCellInventory.getStackInSlot(0);
    }

    private static boolean isItemCell(ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() instanceof ItemBasicStorageCell
            || stack.getItem() instanceof ItemVoidStorageCell && !(stack.getItem() instanceof ItemFluidVoidStorageCell);
    }

    public static long countIn(MTEMultiBlockBase machine) {
        // Use the same valid, deduplicated item views as ejection, including native per-layer hatch lists.
        return machine.getOutputBusses()
            .stream()
            .filter(bus -> bus instanceof MEOutputItemView)
            .distinct()
            .count();
    }

    @Override
    public ItemStack getVisual() {
        // Registration constructs the native provider before the enum stack is bound.
        return getStackForm(1);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("betterexperience.machine.me_output_assembly.desc.0") };
    }

    @Override
    public void onFirstTick(IGregTechTileEntity tile) {
        super.onFirstTick(tile);
        itemOutput.getProvider()
            .updateState();
    }

    @Override
    public void onPostTick(IGregTechTileEntity tile, long tick) {
        super.onPostTick(tile, tick);
        itemOutput.getProvider()
            .onPostTick(tile, tick);
    }

    @Override
    @MENetworkEventSubscribe
    public void updateCell(MENetworkChannelsChanged event) {
        super.updateCell(event);
        itemOutput.getProvider()
            .updateCell();
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(StorageChannel channel) {
        return channel == StorageChannel.ITEMS ? itemOutput.getProvider()
            .getCellArray(channel) : super.getCellArray(channel);
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        saveItemOutput(tag);
    }

    private void saveItemOutput(NBTTagCompound tag) {
        NBTTagCompound items = new NBTTagCompound();
        itemOutput.getProvider()
            .saveNBTData(items);
        // Only the inherited fluid provider owns proxy persistence and lifecycle.
        items.removeTag("proxy");
        tag.setTag(ITEM_OUTPUT_TAG, items);
        if (getItemCell() != null) tag.setTag(ITEM_CELL_TAG, GTUtility.saveItem(getItemCell()));
        else tag.removeTag(ITEM_CELL_TAG);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        itemCellInventory.setStackInSlot(0, GTUtility.loadItem(tag.getCompoundTag(ITEM_CELL_TAG)));
        itemOutput.getProvider()
            .loadNBTData(tag.getCompoundTag(ITEM_OUTPUT_TAG));
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        // Preserve pending outputs on pickup. The extra item cell stays attached to the dropped machine;
        // the fluid cell remains in the native inventory and follows GT's normal inventory drops.
        getProvider().saveNBTData(tag);
        tag.removeTag("proxy");
        saveItemOutput(tag);
    }

    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return COPY_ID;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound tag = super.getCopiedData(player);
        tag.setTag(
            ITEM_OUTPUT_TAG,
            itemOutput.getProvider()
                .getCopiedData(player));
        return tag;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound tag) {
        if (tag == null || !COPY_ID.equals(tag.getString("type")) || !tag.hasKey(ITEM_OUTPUT_TAG)) return false;
        if (!super.pasteCopiedData(player, tag)) return false;
        boolean pasted = itemOutput.getProvider()
            .pasteCopiedData(player, tag.getCompoundTag(ITEM_OUTPUT_TAG));
        notifyOutputSpaceChanged();
        markDirty();
        return pasted;
    }

    @Override
    public void writeToStream(ByteBuf buffer) {
        super.writeToStream(buffer);
        itemOutput.getProvider()
            .writeToClientPacket(buffer);
    }

    @Override
    public void readFromStream(ByteBuf buffer) {
        super.readFromStream(buffer);
        itemOutput.getProvider()
            .readFromClientPacket(buffer);
    }

    @Override
    public String[] getInfoData() {
        List<String> lines = new ArrayList<>(Arrays.asList(super.getInfoData()));
        lines.addAll(
            Arrays.asList(
                itemOutput.getProvider()
                    .getInfoData(
                        getAEDiagnostics(),
                        "GT5U.infodata.hatch.output_bus_me",
                        stack -> stack.getItem()
                            .getItemStackDisplayName(stack.getItemStack()))));
        return lines.toArray(new String[0]);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setLong(
            "betterexperienceItemCached",
            itemOutput.getProvider()
                .getCachedAmount());
        tag.setLong(
            "betterexperienceItemCapacity",
            itemOutput.getProvider()
                .getCacheCapacity());
        // Both native providers use the same WAILA keys. Keep item details separate from the inherited fluid data.
        NBTTagCompound items = new NBTTagCompound();
        itemOutput.getProvider()
            .getWailaNBTData(player, tile, items, world, x, y, z);
        tag.setTag(ITEM_WAILA_TAG, items);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getWailaBody(ItemStack stack, List<String> lines, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        int originalSize = lines.size();
        super.getWailaBody(stack, lines, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        List<String> nativeLines = new ArrayList<>(lines.subList(originalSize, lines.size()));
        lines.subList(originalSize, lines.size())
            .clear();
        String fluidCapacity = StatCollector.translateToLocalFormatted(
            "GT5U.waila.hatch.outputme.fluid_cache_capacity",
            NumberFormatUtil.formatNumber(tag.getLong("cacheCapacity")));
        String itemCapacity = getItemCapacity(tag);
        List<String> itemCacheLines = getItemCacheLines(tag.getCompoundTag(ITEM_WAILA_TAG), false);
        int capacityIndex = nativeLines.indexOf(fluidCapacity);
        if (capacityIndex < 0 || capacityIndex + 1 >= nativeLines.size()) {
            if (!nativeLines.isEmpty()) lines.addAll(nativeLines.subList(0, nativeLines.size() - 1));
            lines.add(itemCapacity);
            lines.addAll(itemCacheLines);
            if (!nativeLines.isEmpty()) lines.add(nativeLines.get(nativeLines.size() - 1));
            return;
        }

        // Group the two capacities, each on its own line, before either cache's detail.
        lines.add(fluidCapacity);
        lines.add(itemCapacity);
        // Move only the native flush line after both cache lists; keep its original text unchanged.
        lines.addAll(nativeLines.subList(capacityIndex + 1, nativeLines.size() - 1));
        lines.addAll(itemCacheLines);
        lines.add(nativeLines.get(nativeLines.size() - 1));
        // Keep base hatch information outside the two adjacent cache sections.
        lines.addAll(nativeLines.subList(0, capacityIndex));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getWailaAdvancedBody(ItemStack stack, List<String> lines, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaAdvancedBody(stack, lines, accessor, config);
        NBTTagCompound items = accessor.getNBTData()
            .getCompoundTag(ITEM_WAILA_TAG);
        if (!items.hasKey("cacheCount")) return;

        lines.add(
            StatCollector.translateToLocalFormatted(
                "betterexperience.machine.me_output_assembly.item_cache.cell",
                items.getInteger("cacheCount")));
        lines.addAll(getItemCacheLines(items, true));
    }

    @SideOnly(Side.CLIENT)
    private static List<String> getItemCacheLines(NBTTagCompound tag, boolean storageCell) {
        List<String> lines = new ArrayList<>();
        int stackCount = tag.getInteger(storageCell ? "cacheCount" : "stackCount");
        if (stackCount == 0) {
            lines.add(StatCollector.translateToLocal("betterexperience.machine.me_output_assembly.item_cache.empty"));
            return lines;
        }
        NBTTagList stacks = tag.getTagList(storageCell ? "cacheStacks" : "stacks", 10);
        for (int i = 0; i < stacks.tagCount(); i++) {
            NBTTagCompound entry = stacks.getCompoundTagAt(i);
            ItemStack item = ItemStack.loadItemStackFromNBT(entry);
            if (item != null) lines.add(GTWaila.getStackListLine(item.getDisplayName(), entry.getLong("Amount")));
        }
        if (stackCount > stacks.tagCount()) {
            lines.add(
                StatCollector.translateToLocalFormatted(
                    "betterexperience.machine.me_output_assembly.item_cache.more",
                    stackCount - stacks.tagCount()));
        }
        return lines;
    }

    @SideOnly(Side.CLIENT)
    private static String getItemCapacity(NBTTagCompound tag) {
        return StatCollector.translateToLocalFormatted(
            "betterexperience.machine.me_output_assembly.item_cache",
            NumberFormatUtil.formatNumber(tag.getLong("betterexperienceItemCached")),
            NumberFormatUtil.formatNumber(tag.getLong("betterexperienceItemCapacity")));
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager syncManager, UISettings settings) {
        return new MEOutputAssemblyGui(this).build(guiData, syncManager, settings);
    }
}
