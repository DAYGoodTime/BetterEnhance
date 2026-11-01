/*
 * SPDX-License-Identifier: GPL-3.0-only
 * Adapted from GT_TileEntity_PreciseHighEnergyPhotonicQuantumMaster by Nxer and the TST contributors.
 * Source: https://github.com/Nxer/Twist-Space-Technology-Mod
 * Modified for BetterGTExperience: project base, tier-scaled parallelism, native modes and tooltip.
 * This adaptation is distributed under GNU GPL v3; see THIRD_PARTY_NOTICES.md.
 */
package com.day.betterexperience.common.machine.multiblock;

import static com.Nxer.TwistSpaceTechnology.common.init.TstBlocks.PhotonControllerUpgrade;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.Parallel_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.Parallel_PhCMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.SpeedUpMultiplier_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.machine.ValueEnum.SpeedUpMultiplier_PhCMode_PreciseHighEnergyPhotonicQuantumMaster;
import static com.Nxer.TwistSpaceTechnology.common.recipeMap.GTCMRecipe.PreciseHighEnergyPhotonicQuantumMasterRecipeMap;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.Nxer.TwistSpaceTechnology.common.block.meta.casing.PhotonControllerUpgradeCasing;
import com.Nxer.TwistSpaceTechnology.common.machine.MachineTexture.TSTControllerTextures;
import com.Nxer.TwistSpaceTechnology.util.TSTUtils;
import com.Nxer.TwistSpaceTechnology.util.text.TSTMultiblockTooltipBuilder;
import com.Nxer.TwistSpaceTechnology.util.text.TSTSharedLocalization;
import com.day.betterexperience.common.machine.multiblock.base.BGTEMultiblockBase;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class BetterTSTPreciseHighEnergyPhotonicQuantumMaster
    extends BGTEMultiblockBase<BetterTSTPreciseHighEnergyPhotonicQuantumMaster> {

    private static IStructureDefinition<BetterTSTPreciseHighEnergyPhotonicQuantumMaster> structureDefinition;

    // spotless:off
    private static final String[][] SHAPE_MAIN = new String[][] {
        { "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "               ", "               ", "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "     DDDDD     " },
        { "     DDDDD     ", "D   DCCCCCD   D", "DEEEEEEEEEEEEED", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DEEEEEEEEEEEEED", "DDDDDCCCCCDDDDD", "     DAAAD     " },
        { "     DMMMD     ", "DDDDDCCCCCDDDDD", "DBBBBBBBBBBBBBD", " I           I ", " I           I ", " I           I ", "DBBBBBBBBBBBBBD", "DDDDDCCCCCDDDDD", "DDDDDDAAADDDDDD" },
        { "     DM~MD     ", "DDDDDCCCCCDDDDD", "DCCCCCCCCCCCCCD", " I           I ", " I           I ", " I           I ", "DCCCCCCCCCCCCCD", "DDDDDCCCCCDDDDD", "DAAAAAAAAAAAAAD" },
        { "     DMMMD     ", "DDDDDCCCCCDDDDD", "DBBBBBBBBBBBBBD", " I           I ", " I           I ", " I           I ", "DBBBBBBBBBBBBBD", "DDDDDCCCCCDDDDD", "DDDDDDAAADDDDDD" },
        { "     DDDDD     ", "D   DCCCCCD   D", "DEEEEEEEEEEEEED", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DXXXXXXXXXXXXXD", "DEEEEEEEEEEEEED", "DDDDDCCCCCDDDDD", "     DAAAD     " },
        { "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "               ", "               ", "               ", "DDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDD", "     DDDDD     " } };
    // spotless:on

    private int totalSpeedIncrement;
    private int upgradeTier = -1;
    private boolean mixedUpgradeTiers;
    private boolean perfectOverclock;

    public BetterTSTPreciseHighEnergyPhotonicQuantumMaster(int id, String name, String regional) {
        super(id, name, regional);
    }

    public BetterTSTPreciseHighEnergyPhotonicQuantumMaster(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new BetterTSTPreciseHighEnergyPhotonicQuantumMaster(mName);
    }

    @Override
    public IStructureDefinition<BetterTSTPreciseHighEnergyPhotonicQuantumMaster> getStructureDefinition() {
        if (structureDefinition == null) {
            structureDefinition = StructureDefinition.<BetterTSTPreciseHighEnergyPhotonicQuantumMaster>builder()
                .addShape("main", transpose(SHAPE_MAIN))
                .addElement('B', ofBlock(GregTechAPI.sBlockCasings2, 5))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 9))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings8, 7))
                .addElement('E', ofBlock(GregTechAPI.sBlockCasings9, 1))
                .addElement(
                    'M',
                    GTStructureUtility.buildHatchAdder(BetterTSTPreciseHighEnergyPhotonicQuantumMaster.class)
                        .atLeast(Maintenance)
                        .hint(1)
                        .casingIndex(183)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 7))
                .addElement(
                    'I',
                    GTStructureUtility.buildHatchAdder(BetterTSTPreciseHighEnergyPhotonicQuantumMaster.class)
                        .atLeast(InputBus, InputHatch, OutputBus, OutputHatch)
                        .hint(2)
                        .casingIndex(183)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 7))
                .addElement(
                    'X',
                    GTStructureUtility.buildHatchAdder(BetterTSTPreciseHighEnergyPhotonicQuantumMaster.class)
                        .atLeast(Energy.or(ExoticEnergy))
                        .hint(3)
                        .casingIndex(1024)
                        .buildAndChain(sBlockCasingsTT, 0))
                .addElement(
                    'A',
                    ofChain(
                        upgradeElement(0),
                        upgradeElement(1),
                        upgradeElement(2),
                        upgradeElement(3),
                        upgradeElement(4),
                        upgradeElement(5),
                        upgradeElement(6),
                        upgradeElement(7),
                        upgradeElement(8),
                        upgradeElement(9),
                        upgradeElement(10),
                        upgradeElement(11),
                        upgradeElement(12),
                        upgradeElement(13),
                        ofBlock(GregTechAPI.sBlockCasings8, 7)))
                .build();
        }
        return structureDefinition;
    }

    private static IStructureElement<BetterTSTPreciseHighEnergyPhotonicQuantumMaster> upgradeElement(int meta) {
        return onElementPass(machine -> {
            machine.totalSpeedIncrement += PhotonControllerUpgradeCasing.speedIncrement[meta];
            if (meta >= 11) machine.perfectOverclock = true;
            if (machine.upgradeTier == -1) {
                machine.upgradeTier = meta;
            } else if (machine.upgradeTier != meta) {
                // Mixed tiers retain the original upgrades, but do not increase parallelism.
                machine.mixedUpgradeTiers = true;
            }
        }, ofBlock(PhotonControllerUpgrade, meta));
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece("main", stack, hintsOnly, 7, 3, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int budget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece("main", stack, 7, 3, 0, budget, env, false, true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity tile, ItemStack stack, List<StructureError> errors) {
        totalSpeedIncrement = 0;
        upgradeTier = -1;
        mixedUpgradeTiers = false;
        perfectOverclock = false;
        checkPiece("main", 7, 3, 0, errors);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == 1 ? PreciseHighEnergyPhotonicQuantumMasterRecipeMap : RecipeMaps.laserEngraverRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.laserEngraverRecipes, PreciseHighEnergyPhotonicQuantumMasterRecipeMap);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new MTEMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_SINGULARITY);
    }

    @Override
    public String getMachineModeName() {
        return text("mode." + machineMode);
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            setMachineMode(nextMachineMode());
            GTUtility.sendChatToPlayer(player, getMachineModeName());
        }
    }

    private int getParallelMultiplier() {
        return upgradeTier < 0 || mixedUpgradeTiers ? 1 : 1 << (upgradeTier + 1);
    }

    @Override
    public int getMaxParallelRecipes() {
        int baseParallel = machineMode == 1 ? Parallel_PhCMode_PreciseHighEnergyPhotonicQuantumMaster
            : Parallel_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster;
        return baseParallel * getParallelMultiplier();
    }

    @Override
    public double getDurationModifier() {
        int speedMultiplier = machineMode == 1 ? SpeedUpMultiplier_PhCMode_PreciseHighEnergyPhotonicQuantumMaster
            : SpeedUpMultiplier_LaserEngraverMode_PreciseHighEnergyPhotonicQuantumMaster;
        return 10000D / (10000D + totalSpeedIncrement) / speedMultiplier;
    }

    @Override
    public boolean getPerfectOC() {
        return perfectOverclock;
    }

    @Override
    public String[] getInfoData() {
        String[] original = super.getInfoData();
        String[] info = Arrays.copyOf(original, original.length + 4);
        info[original.length] = getMachineModeName();
        info[original.length + 1] = text("status.parallel", getMaxParallelRecipes(), getParallelMultiplier());
        info[original.length + 2] = text("status.speed", totalSpeedIncrement / 100.0);
        info[original.length + 3] = text("status.perfect_oc", text(perfectOverclock ? "enabled" : "disabled"));
        return info;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tile, ForgeDirection side, ForgeDirection facing, int colorIndex,
        boolean active, boolean redstone) {
        return TSTControllerTextures.getTexture(
            side,
            facing,
            active,
            Textures.BlockIcons.getCasingTextureForId(183),
            OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE,
            OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW,
            OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE,
            OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW);
    }

    private static String text(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("betterexperience.machine.photonic_quantum_master." + key, args);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new TSTMultiblockTooltipBuilder()
            .addMachineType(
                TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.machine_type"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.01"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.02"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.03"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.04"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.05"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.06"))
            .addInfo(text("parallel_upgrade"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.07"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.08"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.09"))
            .addInfo(TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.info.10"))
            .beginStructureBlock(SHAPE_MAIN)
            .addController(TSTSharedLocalization.Structure.textFrontCenter)
            .addCasingInfoRange(TSTSharedLocalization.Structure.textCasingAdvIrPlated, 296, 347, false)
            .addCasingInfoRange(TSTSharedLocalization.Structure.textCasingTT_0, 0, 78, false)
            .addCasingInfoRange(
                TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.structure.01"),
                0,
                25,
                false)
            .addInputHatch(TSTSharedLocalization.Structure.textCenterOfLRSides, 2)
            .addOutputHatch(TSTSharedLocalization.Structure.textCenterOfLRSides, 2)
            .addInputBus(TSTSharedLocalization.Structure.textCenterOfLRSides, 2)
            .addOutputBus(TSTSharedLocalization.Structure.textCenterOfLRSides, 2)
            .addEnergyHatch(
                TSTUtils.tr("tst.common.machine.PreciseHighEnergyPhotonicQuantumMaster.tooltip.structure.02"),
                3)
            .toolTipFinisher();
    }
}
