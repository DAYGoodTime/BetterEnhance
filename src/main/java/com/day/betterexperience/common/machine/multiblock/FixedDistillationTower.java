package com.day.betterexperience.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.day.betterexperience.common.machine.multiblock.base.BGTEMultiblockBase;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.casing.Casings;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.enums.VoidingMode;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICasingTextureProvider;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.FluidEjectionHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

@IMetaTileEntity.SkipGenerateDescription
public class FixedDistillationTower extends BGTEMultiblockBase<FixedDistillationTower>
    implements ICasingTextureProvider {

    private static final String LANG = "betterexperience.machine.fixed_distillation_tower.";
    private static final String TOWER = "tower";
    private static final int CASING_TEXTURE = 49;
    private static final int DISTILLERY = 1;
    private static final int PARALLEL_BITS_PER_TIER = 2;
    private static final int PARALLEL_OFFSET = 2;
    private static final int MIN_PARALLEL = 1;
    private static final double EU_MULTIPLIER = 0.5;
    private static final double DURATION_MULTIPLIER = 0.2;

    // spotless:off
    private static final IStructureDefinition<FixedDistillationTower> STRUCTURE = StructureDefinition
        .<FixedDistillationTower>builder()
        .addShape(TOWER, transpose(new String[][] {
            { "CCC", "CCC", "CCC" },
            { "CCC", "C-C", "CCC" },
            { "CCC", "C-C", "CCC" },
            { "CCC", "C-C", "CCC" },
            { "CCC", "C-C", "CCC" },
            { "CCC", "C-C", "CCC" },
            { "C~C", "CCC", "CCC" },
        }))
        .addElement(
            'C',
            buildHatchAdder(FixedDistillationTower.class)
                .atLeast(Energy.or(ExoticEnergy), Maintenance, InputHatch, OutputHatch, InputBus, OutputBus)
                .casingIndex(CASING_TEXTURE)
                .hint(1)
                .buildAndChain(Casings.CleanStainlessSteelMachineCasing.asElement()))
        .build();
    // spotless:on

    public FixedDistillationTower(int id, String name, String regional) {
        super(id, name, regional);
    }

    private FixedDistillationTower(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new FixedDistillationTower(mName);
    }

    @Override
    public IStructureDefinition<FixedDistillationTower> getStructureDefinition() {
        return STRUCTURE;
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (direction, rotation, flip) -> direction.offsetY == 0 && rotation.isNotRotated()
            && !flip.isVerticallyFliped();
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return false;
    }

    @Override
    public void checkMachine(IGregTechTileEntity tile, ItemStack stack, List<StructureError> errors) {
        if (!checkPiece(TOWER, 1, 6, 0, errors)) return;
        checkHasAnyEnergy(errors);
        checkHatchExact(errors, Maintenance, 1);
        checkHasInputHatch(errors);
        checkHasOutputHatch(errors);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(TOWER, stack, hintsOnly, 1, 6, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int budget, ISurvivalBuildEnvironment env) {
        return mMachine ? -1 : survivalBuildPiece(TOWER, stack, 1, 6, 0, budget, env, false, true);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == DISTILLERY ? RecipeMaps.distilleryRecipes : RecipeMaps.distillationTowerRecipes;
    }

    @Override
    public @NotNull Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return List.of(RecipeMaps.distillationTowerRecipes, RecipeMaps.distilleryRecipes);
    }

    @Override
    public double getEUtDiscount() {
        return EU_MULTIPLIER;
    }

    @Override
    public double getDurationModifier() {
        return DURATION_MULTIPLIER;
    }

    @Override
    public int getMaxParallelRecipes() {
        // GT's voltage tier starts at zero (ULV) and tops out at 15.
        return Math.max(MIN_PARALLEL, (1 << (PARALLEL_BITS_PER_TIER * getSupplyTier())) - PARALLEL_OFFSET);
    }

    private int getSupplyTier() {
        long highestVoltage = 0;
        for (var hatch : getExoticAndNormalEnergyHatchList()) {
            highestVoltage = Math.max(
                highestVoltage,
                hatch.getBaseMetaTileEntity()
                    .getInputVoltage());
        }
        return GTUtility.getTier(highestVoltage);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Override
            protected @NotNull OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                // Do not let sub-tick overclocks raise the parallel limit set in the power panel.
                return super.createOverclockCalculator(recipe)
                    .setDurationUnderOneTickSupplier(() -> Math.max(1, recipe.mDuration * getDurationModifier()));
            }
        }.setMaxParallelSupplier(this::getTrueParallel)
            .setEuModifierSupplier(this::getEUtDiscount)
            .setSpeedBonusSupplier(this::getDurationModifier);
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return !disableMaintenance && hasMaintenanceChecks;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public Set<VoidingMode> getAllowedVoidingModes() {
        return VoidingMode.ITEM_ONLY_MODES;
    }

    @Override
    public boolean protectsExcessFluid() {
        return true;
    }

    @Override
    protected void runMachine(IGregTechTileEntity tile, long tick) {
        if (mMaxProgresstime > 0 && mProgresstime >= mMaxProgresstime - 1
            && mOutputFluids != null
            && mOutputFluids.length > 0) {
            // The available destinations can change after the recipe starts. Keep the pending outputs until they fit.
            var outputs = Arrays.asList(mOutputFluids);
            if (new FluidEjectionHelper(getOutputHatches(), true).ejectFluids(outputs, 1) == 0) {
                setCheckRecipeResult(CheckRecipeResultRegistry.FLUID_OUTPUT_FULL);
                return;
            }
            setCheckRecipeResult(CheckRecipeResultRegistry.SUCCESSFUL);
        }
        super.runMachine(tile, tick);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public String getMachineModeKey() {
        return LANG + (machineMode == DISTILLERY ? "mode.distillery" : "mode.tower");
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        setMachineMode(nextMachineMode());
        GTUtility.sendChatTrans(player, "GT5U.MULTI_MACHINE_CHANGE", new ChatComponentTranslation(getMachineModeKey()));
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new MTEMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DISTILLATION_TOWER,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DISTILLING);
    }

    @Override
    public ITexture getCasingTexture() {
        return BlockIcons.getCasingTextureForId(CASING_TEXTURE);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tile, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        if (side != facing) return new ITexture[] { getCasingTexture() };
        return new ITexture[] { getCasingTexture(), TextureFactory.builder()
            .addIcon(
                active ? BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE
                    : BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER)
            .extFacing()
            .build(),
            TextureFactory.builder()
                .addIcon(
                    active ? BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW
                        : BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                .extFacing()
                .glow()
                .build() };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        String anyCasing = StatCollector.translateToLocal("gt.mbtt.structure.any_casing");
        return new MultiblockTooltipBuilder().addMachineType(StatCollector.translateToLocal(LANG + "type"))
            .addMarkdown(
                new ResourceLocation("betterexperience", "fixed-distillation-tower"),
                Map.of(
                    "parallelBase",
                    1 << PARALLEL_BITS_PER_TIER,
                    "parallelOffset",
                    PARALLEL_OFFSET,
                    "minParallel",
                    MIN_PARALLEL,
                    "durationPercent",
                    Math.round(DURATION_MULTIPLIER * 100),
                    "energyPercent",
                    Math.round(EU_MULTIPLIER * 100)))
            .beginStructureBlock(3, 7, 3, true)
            .addController(StatCollector.translateToLocal("gt.mbtt.structure.front_bottom_center"))
            .addCasing("0–53", Casings.CleanStainlessSteelMachineCasing.getLocalizedName(), false)
            .addEnergyHatch("0+", anyCasing, 1)
            .addMiscHatch("0+", StatCollector.translateToLocal("GT5U.MBTT.ExoticEnergyHatch"), anyCasing, 1)
            .addStructureInfo(StatCollector.translateToLocal(LANG + "energy_requirement"))
            .addMaintenanceHatch("1", anyCasing, 1)
            .addInputHatch("1+", anyCasing, 1)
            .addOutputHatch("1+", anyCasing, 1)
            .addInputBus("0+", anyCasing, 1)
            .addOutputBus("0+", anyCasing, 1)
            .addAir(StatCollector.translateToLocal("gt.mbtt.structure.interior"))
            .toolTipFinisher();
    }
}
