package com.day.betterexperience.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.Pair;

import com.day.betterexperience.common.machine.multiblock.base.BGTESteamMultiblockBase;
import com.day.betterexperience.common.machine.multiblock.base.SteamMachineTier;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

@IMetaTileEntity.SkipGenerateDescription
public class SteamIndustrialElectrolyzer extends BGTESteamMultiblockBase<SteamIndustrialElectrolyzer> {

    private static final String MAIN = "main";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 2;
    private static final int OFFSET_Z = 1;
    private static IStructureDefinition<SteamIndustrialElectrolyzer> structure;

    // spotless:off
    private static final String[][] SHAPE = {
        { " DDD ", "     ", "     ", "     ", " FFF " },
        { "DDDDD", " ADA ", " A~A ", " ADA ", "FFFFF" },
        { "DCDBD", " CDB ", " CDB ", " CDB ", "FFFFF" },
        { "DDDDD", " ADA ", " ADA ", " ADA ", "FFFFF" },
        { " DDD ", "     ", "     ", "     ", " FFF " }
    };
    // spotless:on

    public SteamIndustrialElectrolyzer(int id, String name, String regional) {
        super(id, name, regional);
    }

    public SteamIndustrialElectrolyzer(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new SteamIndustrialElectrolyzer(mName);
    }

    @Override
    public IStructureDefinition<SteamIndustrialElectrolyzer> getStructureDefinition() {
        if (structure == null) {
            structure = StructureDefinition.<SteamIndustrialElectrolyzer>builder()
                .addShape(MAIN, SHAPE)
                .addElement('A', GTStructureChannels.TIER_MACHINE_CASING.use(ofBlocksTiered((block, meta) -> {
                    if (block != GregTechAPI.sBlockFrames) return null;
                    if (meta == Materials.Bronze.mMetaItemSubID) return SteamMachineTier.BRONZE;
                    if (meta == Materials.Steel.mMetaItemSubID) return SteamMachineTier.STEEL;
                    return null;
                },
                    ImmutableList.of(
                        Pair.of(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID),
                        Pair.of(GregTechAPI.sBlockFrames, (int) Materials.Steel.mMetaItemSubID)),
                    SteamMachineTier.UNFORMED,
                    (machine, tier) -> machine.checkedTier = tier,
                    machine -> machine.checkedTier)))
                .addElement(
                    'B',
                    GTStructureChannels.TIER_MACHINE_CASING
                        .use(tieredCasing(Casings.BronzePipeCasing, Casings.SteelPipeCasing)))
                .addElement(
                    'C',
                    GTStructureChannels.TIER_MACHINE_CASING
                        .use(tieredCasing(Casings.BronzePipeCasing, Casings.SteelPipeCasing)))
                .addElement(
                    'D',
                    GTStructureChannels.TIER_MACHINE_CASING.use(
                        ofChain(
                            buildSteamSupply(SteamIndustrialElectrolyzer.class)
                                .casingIndex(Casings.BronzePlatedBricks.textureId)
                                .hint(1)
                                .build(),
                            tieredCasing(Casings.BronzePlatedBricks, Casings.SolidSteelMachineCasing),
                            buildHatchAdder(SteamIndustrialElectrolyzer.class)
                                .atLeast(
                                    SteamHatchElement.InputBus_Steam,
                                    SteamHatchElement.OutputBus_Steam,
                                    InputBus,
                                    OutputBus,
                                    InputHatch,
                                    OutputHatch)
                                .casingIndex(Casings.BronzePlatedBricks.textureId)
                                .hint(1)
                                .build())))
                .addElement(
                    'F',
                    GTStructureChannels.TIER_MACHINE_CASING
                        .use(tieredCasing(Casings.BronzeFireboxCasing, Casings.SteelFireboxCasing)))
                .build();
        }
        return structure;
    }

    @Override
    public void checkMachine(IGregTechTileEntity tile, ItemStack stack, List<StructureError> errors) {
        beginSteamStructureCheck();
        if (!checkPiece(MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors)) return;
        finishSteamStructureCheck(errors);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(MAIN, stack, hintsOnly, OFFSET_X, OFFSET_Y, OFFSET_Z);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int budget, ISurvivalBuildEnvironment environment) {
        if (mMachine) return -1;
        return survivalBuildPiece(MAIN, stack, OFFSET_X, OFFSET_Y, OFFSET_Z, budget, environment, false, true);
    }

    @Override
    protected int getBaseParallel() {
        return 16;
    }

    @Override
    protected double getBaseEnergyModifier() {
        return 0.5;
    }

    @Override
    public int getTierRecipes() {
        ItemStack controllerItem = getControllerSlot();
        return controllerItem != null && controllerItem.stackSize > 0
            && controllerItem.isItemEqual(GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 1L))
                ? VoltageIndex.HV
                : super.getTierRecipes();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.electrolyzerNonCellRecipes;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gt.mbtt.machine_type.electrolyzer");
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return TexturesGtBlock.oMCDIndustrialElectrolyzer;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return TexturesGtBlock.oMCDIndustrialElectrolyzerActive;
    }

    @Override
    protected IIconContainer getInactiveGlowOverlay() {
        return TexturesGtBlock.oMCDIndustrialElectrolyzerGlow;
    }

    @Override
    protected IIconContainer getActiveGlowOverlay() {
        return TexturesGtBlock.oMCDIndustrialElectrolyzerActiveGlow;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        String prefix = "betterexperience.machine.steam_industrial_electrolyzer.";
        String hatchPosition = StatCollector.translateToLocal(prefix + "hatch_position");
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addMarkdown(
                new ResourceLocation("betterexperience", "steam-industrial-electrolyzer"),
                Map.of(
                    "bronzeParallel",
                    SteamMachineTier.BRONZE.getParallel(getBaseParallel()),
                    "steelParallel",
                    SteamMachineTier.STEEL.getParallel(getBaseParallel()),
                    "bronzeSpeed",
                    SteamMachineTier.BRONZE.getSpeedMultiplier() * 100 + "%",
                    "steelSpeed",
                    SteamMachineTier.STEEL.getSpeedMultiplier()))
            .beginStructureBlock(5, 5, 5, false)
            .addController(StatCollector.translateToLocal("gt.mbtt.structure.front_center_3rd_layer"))
            .addCasing("0-24", StatCollector.translateToLocal(prefix + "plated_casing"), false)
            .addCasing("21", StatCollector.translateToLocal(prefix + "firebox"), false)
            .addCasing("8", StatCollector.translateToLocal(prefix + "pipe_casing"), false)
            .addCasing("12", StatCollector.translateToLocal(prefix + "frame"), false)
            .addMiscHatch("1+", StatCollector.translateToLocal(prefix + "steam_supply"), hatchPosition, 1)
            .addInputBus("1+", hatchPosition, 1)
            .addOutputBus("1+", hatchPosition, 1)
            .addInputHatch("0+", hatchPosition, 1)
            .addOutputHatch("0+", hatchPosition, 1)
            .toolTipFinisher();
    }
}
