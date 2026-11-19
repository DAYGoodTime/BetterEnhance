/*
 * SPDX-License-Identifier: GPL-3.0-only
 * Adapted from TST_NetherInterface by Nxer and the Twist Space Technology contributors.
 * Source: https://github.com/Nxer/Twist-Space-Technology-Mod
 * Modified for BetterGTExperience: project base, registration, native tooltip and bounded arithmetic.
 * This adaptation is distributed under GNU GPL v3; see THIRD_PARTY_NOTICES.md.
 */
package com.day.betterexperience.common.machine.multiblock;

import static com.Nxer.TwistSpaceTechnology.common.init.TstBlocks.MetaBlockCasing02;
import static com.Nxer.TwistSpaceTechnology.common.recipeMap.GTCMRecipe.NetherInterfaceVisualRecipeMap;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_GLOW;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.Nxer.TwistSpaceTechnology.common.api.random.XSTR;
import com.Nxer.TwistSpaceTechnology.common.machine.MachineTexture.TSTControllerTextures;
import com.Nxer.TwistSpaceTechnology.common.machine.MiscHelper;
import com.Nxer.TwistSpaceTechnology.common.machine.TST_NetherInterface;
import com.Nxer.TwistSpaceTechnology.config.Config;
import com.day.betterexperience.common.machine.multiblock.base.BGTEMultiblockBase;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.ParallelHelper;

public class BetterTSTNetherInterface extends BGTEMultiblockBase<BetterTSTNetherInterface> {

    private static IStructureDefinition<BetterTSTNetherInterface> structureDefinition;

    // spotless:off
    private static final String[][] SHAPE_MAIN = new String[][]{
        {"               ", " AAAAAAAAAAAAA ", " AAAAAAAAAAAAA ", " AAAAAAAAAAAAA "},
        {"               ", " A           A ", " CCCCCCCCCCCCC ", " A           A "},
        {"               ", " BCCCCCCCCCCCB ", " CDDDDDDDDDDDC ", " BCCCCCCCCCCCB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"               ", " BC         CB ", " CD         DC ", " BC         CB "},
        {"      A~A      ", " BCCCCCCCCCCCB ", " CDDDDDDDDDDDC ", " BCCCCCCCCCCCB "},
        {"      AAA      ", "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA"},
        {"      AAA      ", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA"}};
    // spotless:on

    public BetterTSTNetherInterface(int id, String name, String regional) {
        super(id, name, regional);
    }

    public BetterTSTNetherInterface(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new BetterTSTNetherInterface(mName);
    }

    @Override
    public IStructureDefinition<BetterTSTNetherInterface> getStructureDefinition() {
        if (structureDefinition == null) {
            structureDefinition = StructureDefinition.<BetterTSTNetherInterface>builder()
                .addShape("main", transpose(SHAPE_MAIN))
                .addElement(
                    'A',
                    HatchElementBuilder.<BetterTSTNetherInterface>builder()
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                        .adder(BetterTSTNetherInterface::addToMachineList)
                        .hint(1)
                        .casingIndex(16)
                        .buildAndChain(ofBlock(GregTechAPI.sBlockCasings2, 0)))
                .addElement('B', ofFrame(Materials.Obsidian))
                .addElement('C', ofBlock(MetaBlockCasing02, 5))
                .addElement('D', ofBlock(Blocks.obsidian, 0))
                .build();
        }
        return structureDefinition;
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece("main", stack, hintsOnly, 7, 13, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int budget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece("main", stack, 7, 13, 0, budget, env, false, true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity tile, ItemStack stack, List<StructureError> errors) {
        checkPiece("main", 7, 13, 0, errors);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return NetherInterfaceVisualRecipeMap;
    }

    @Override
    protected boolean supportsCraftingMEBuffer() {
        return false;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        long inputEut = getMaxInputEu();
        int energyCost = Config.BasicEnergyCost_NetherInterface;
        int waterCost = Config.BasicDistilledWaterCost_NetherInterface;
        if (inputEut < energyCost * 3L) {
            return CheckRecipeResultRegistry.insufficientPower(energyCost * 3L);
        }
        ArrayList<FluidStack> fluids = getStoredFluids();
        if (fluids.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        long waterAmount = 0;
        FluidStack distilledWater = MiscHelper.distilledWater;
        for (FluidStack fluid : fluids) {
            if (fluid != null && fluid.amount > 0 && distilledWater.isFluidEqual(fluid)) {
                waterAmount += fluid.amount;
            }
        }
        long parallelLimit = Math.min(getTrueParallel(), inputEut / energyCost - 2);
        parallelLimit = Math.min(parallelLimit, waterAmount / waterCost);
        int parallel = (int) parallelLimit;
        if (parallel <= 0) return CheckRecipeResultRegistry.NO_RECIPE;
        if (TST_NetherInterface.ItemRandomGetter == null) TST_NetherInterface.initStatics();
        ArrayList<ItemStack> outputs = new ArrayList<>();
        for (int i = 0; i < Config.GenerateStackEveryProcessing_NetherInterface; i++) {
            ItemStack output = TST_NetherInterface.ItemRandomGetter.getOne();
            ParallelHelper.addItemsLong(outputs, output, (long) output.stackSize * parallel);
        }
        long fluidAmount = (long) parallel * waterCost;
        ArrayList<FluidStack> fluidOutputs = new ArrayList<>();
        if (XSTR.XSTR_INSTANCE.nextInt(10000) < Config.OutputHellishMetalPercent_NetherInterface * 100) {
            ParallelHelper.addFluidsLong(fluidOutputs, Materials.HellishMetal.getMolten(1), 288L * parallel);
        }
        ParallelHelper.addFluidsLong(fluidOutputs, Materials.PoorNetherWaste.getFluid(1), fluidAmount);
        // consume input after calc outputs
        if (!depleteInput(distilledWater, fluidAmount)) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        mOutputItems = outputs.toArray(new ItemStack[0]);
        mOutputFluids = fluidOutputs.toArray(new FluidStack[0]);
        lEUt = -energyCost * (2L + parallel);
        mMaxProgresstime = Config.CycleTime_NetherInterface;
        lastParallel = parallel;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    protected long getActualEnergyUsage() {
        return -lEUt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tile, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        return TSTControllerTextures.getTexture(
            side,
            facing,
            active,
            Textures.BlockIcons.casingTexturePages[0][16],
            OVERLAY_FRONT_IMPLOSION_COMPRESSOR,
            OVERLAY_FRONT_IMPLOSION_COMPRESSOR_GLOW,
            OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE,
            OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE_GLOW);
    }

    private static String text(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("betterexperience.machine.nether_interface." + key, args);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        String hatchPosition = text("hatch_position");
        return new MultiblockTooltipBuilder().addMachineType(text("machine_type"))
            .addMarkdown(
                new ResourceLocation("betterexperience", "nether-interface"),
                Map.of(
                    "maxParallel",
                    getMaxParallelRecipes(),
                    "energyCost",
                    Config.BasicEnergyCost_NetherInterface,
                    "waterCost",
                    Config.BasicDistilledWaterCost_NetherInterface,
                    "cycleTicks",
                    Config.CycleTime_NetherInterface))
            .beginStructureBlock(15, 16, 4, true)
            .addCasing("128", text("casing"), false)
            .addCasing("48", text("frame"), false)
            .addCasing("134", text("enhanced_obsidian"), false)
            .addCasing("42", text("obsidian"), false)
            .addInputBus("1+", hatchPosition, 1)
            .addOutputBus("1+", hatchPosition, 1)
            .addInputHatch("1+", hatchPosition, 1)
            .addOutputHatch("1+", hatchPosition, 1)
            .addEnergyHatch("1+", hatchPosition, 1)
            .toolTipFinisher();
    }
}
