package com.ngxdev.anticheat.world;

import com.ngxdev.anticheat.containers.basic.SimpleLocation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Material.*;

public class CollisionHandler {
    private List<Block> blocks;
    private SimpleLocation location;
    private SimpleCollisionBox box;
    private double width, height;
    private double shift;


    public CollisionHandler(List<Block> blocks, SimpleLocation location) {
        this.blocks = blocks;
        this.location = location;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setOffset(double shift) {
        this.shift = shift;
    }

    public boolean isCollidedWith(int bitmask) {
        return false;
    }

    enum BlockData {
        LIQUID(new SimpleCollisionBox(0, 0, 0, 1, 0.9, 0),
                WATER, LAVA, STATIONARY_LAVA, STATIONARY_WATER),
        BREWINGSTAND(new ComplexCollisionBox(
                new SimpleCollisionBox(0.1, 0.0, 0.1, 0.9, 0.1, 0.9),              //base
                new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625) //top
        ), BREWING_STAND),
        DOOR(new DynamicCollisionBox(b -> {
            BlockState state = (BlockState) b.getState();

            BlockFace face = ((Door) state).getFacing();
            //todo;
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 0.2);
        }), WOOD_DOOR, ACACIA_DOOR, BIRCH_DOOR, JUNGLE_DOOR, IRON_DOOR, DARK_OAK_DOOR, SPRUCE_DOOR),
        DEFAULT(new SimpleCollisionBox(0, 0, 0, 1, 1 ,1));

        @Getter
        private CollisionBox box;
        private Material[] materials;

        BlockData(CollisionBox box, Material... materials) {
            this.box = box;
            this.materials = materials;
        }

        public static BlockData getData(Material material) {
            for (BlockData data : values()) {
                for (Material mat : data.materials) if (mat == material) return data;
            }
            return DEFAULT;
        }
    }

    interface CollisionBox {
        boolean isCollided(CollisionBox other, Block block);
    }

    static class SimpleCollisionBox implements CollisionBox {
        double x1, y1, z1, x2, y2, z2;

        public SimpleCollisionBox(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }

        @Override
        public boolean isCollided(CollisionBox other, Block block) {
            if (other instanceof SimpleCollisionBox) {
                SimpleCollisionBox box = (SimpleCollisionBox) other;
                return (box.x2 > this.x1 && box.x1 < this.x2)
                        && ((box.y2 > this.y1 && box.y1 < this.y2)
                        && (box.z2 > this.z1 && box.z1 < this.z2));
            } else throw new IllegalStateException("Attempted to check collision with " + other.getClass().getSimpleName());
        }

        private boolean contains(double min, double max, double value) {
            return min < value && max > value;
        }
    }

    static class ComplexCollisionBox implements CollisionBox {
        private List<CollisionBox> boxes = new ArrayList<>();

        public ComplexCollisionBox(CollisionBox... boxes) {
            Collections.addAll(this.boxes, boxes);
        }

        @Override
        public boolean isCollided(CollisionBox other, Block block) {
            return boxes.stream().anyMatch(box -> box.isCollided(other, block));
        }
    }

    static class DynamicCollisionBox implements CollisionBox {
        private DynamicBox box;

        public DynamicCollisionBox(DynamicBox box) {
            this.box = box;
        }

        @Override
        public boolean isCollided(CollisionBox other, Block block) {
            return box.fetch(block).isCollided(other, block);
        }

        interface DynamicBox {
            CollisionBox fetch(Block block);
        }
    }
}
