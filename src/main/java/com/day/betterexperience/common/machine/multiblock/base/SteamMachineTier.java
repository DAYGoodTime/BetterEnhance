package com.day.betterexperience.common.machine.multiblock.base;

public enum SteamMachineTier {

    UNFORMED(0, 1, 1.0),
    BRONZE(1, 1, 2.5),
    STEEL(2, 2, 3.5);

    private final int id;
    private final int parallelMultiplier;
    private final double speedMultiplier;

    SteamMachineTier(int id, int parallelMultiplier, double speedMultiplier) {
        this.id = id;
        this.parallelMultiplier = parallelMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    public int getId() {
        return id;
    }

    public int getParallel(int baseParallel) {
        return baseParallel * parallelMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getDurationModifier() {
        return 1.0 / speedMultiplier;
    }

    public String getTranslationKey() {
        return "betterexperience.steam.tier." + id;
    }

    public static SteamMachineTier fromId(int id) {
        return switch (id) {
            case 1 -> BRONZE;
            case 2 -> STEEL;
            default -> UNFORMED;
        };
    }
}
