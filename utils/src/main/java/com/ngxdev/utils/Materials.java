package com.ngxdev.utils;

import org.bukkit.Material;

public class Materials {
    private static final int[] MATERIAL_FLAGS = new int[256];

    public static final int SOLID  = 1;
    public static final int LIQUID = 2;
    public static final int LADDER = 4;
    public static final int WALL   = 8;
    public static final int STAIRS = 16;
    public static final int SLABS  = 32;
    public static final int WATER  = 64;
    public static final int LAVA   = 128;

    static {
        for (int i = 0; i < MATERIAL_FLAGS.length; i++) {
            Material material = Material.values()[i];

            if (material.isSolid()) {
                MATERIAL_FLAGS[i] |= SOLID;
            }

            if (material.name().endsWith("_STAIRS")) {
                MATERIAL_FLAGS[i] |= STAIRS;
            }

            if (material.name().contains("SLAB")) {
                MATERIAL_FLAGS[i] |= SLABS;
            }
        }

        // fix some types where isSolid() returns the wrong value
        MATERIAL_FLAGS[Material.SIGN_POST.getId()] = 0;
        MATERIAL_FLAGS[Material.WALL_SIGN.getId()] = 0;
        MATERIAL_FLAGS[Material.DIODE_BLOCK_OFF.getId()] = SOLID;
        MATERIAL_FLAGS[Material.DIODE_BLOCK_ON.getId()] = SOLID;
        MATERIAL_FLAGS[Material.CARPET.getId()] = SOLID;
        MATERIAL_FLAGS[Material.SNOW.getId()] = SOLID;
        MATERIAL_FLAGS[Material.ANVIL.getId()] = SOLID;

        // liquids
        MATERIAL_FLAGS[Material.WATER.getId()] |= LIQUID & WATER;
        MATERIAL_FLAGS[Material.STATIONARY_WATER.getId()] |= LIQUID & WATER;
        MATERIAL_FLAGS[Material.LAVA.getId()] |= LIQUID & WATER;
        MATERIAL_FLAGS[Material.STATIONARY_LAVA.getId()] |= LIQUID & WATER;

        // ladders
        MATERIAL_FLAGS[Material.LADDER.getId()] |= LADDER | SOLID;
        MATERIAL_FLAGS[Material.VINE.getId()] |= LADDER | SOLID;

        // walls
        MATERIAL_FLAGS[Material.FENCE.getId()] |= WALL;
        MATERIAL_FLAGS[Material.FENCE_GATE.getId()] |= WALL;
        MATERIAL_FLAGS[Material.COBBLE_WALL.getId()] |= WALL;
        MATERIAL_FLAGS[Material.NETHER_FENCE.getId()] |= WALL;

        // slabs
        MATERIAL_FLAGS[Material.BED_BLOCK.getId()] |= SLABS;
    }

    private Materials() {
    }

    public static boolean checkFlag(Material material, int flag) {
        return (MATERIAL_FLAGS[material.getId()] & flag) == flag;
    }

}
