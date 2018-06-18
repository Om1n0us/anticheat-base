package com.ngxdev.anticheat.world;

import com.ngxdev.anticheat.containers.basic.SimpleLocation;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.utils.Materials;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Material.*;

@Getter
public class CollisionHandler {
    private List<Block> blocks;
    private SimpleLocation location;
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
        SimpleCollisionBox playerBox = new SimpleCollisionBox()
                .offset(location.getX(), location.getY(), location.getZ())
                .offset(0, shift, 0)
                .expandMax(0, height, 0)
                .expand(width / 2, 0, width / 2);

        for (double x = playerBox.x1; x < playerBox.x2; x += 0.2) {
            for (double y = playerBox.y1; y < playerBox.y2; y += 0.2) {
                for (double z = playerBox.z1; z < playerBox.z2; z += 0.2) {
                    final double fX = x;
                    final double fY = y;
                    final double fZ = z;
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        TinyProtocolHandler.sendPacket(p, new PacketPlayOutWorldParticles(EnumParticle.ENCHANTMENT_TABLE, true, (float) fX, (float) fY, (float) fZ, 0F, 0F, 0F, 0, 1));
                    });
                }
            }
        }

        return blocks.stream()
                .filter(b -> Materials.checkFlag(b.getType(), bitmask))
                .anyMatch(b -> {
                    return BlockData.getData(b.getType()).getBox().isCollided(location, playerBox, b);
                });
    }

    enum BlockData {
        LIQUID(new SimpleCollisionBox(0, 0, 0, 1, 0.9, 0),
                WATER, LAVA, STATIONARY_LAVA, STATIONARY_WATER),
        BREWINGSTAND(new ComplexCollisionBox(
                new SimpleCollisionBox(0, 0.0, 0, 1, 0.1, 1),                      //base
                new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625) //top
        ), BREWING_STAND),
        DOOR(new DynamicCollisionBox(b -> {
            BlockState state = (BlockState) b.getState();

            BlockFace face = ((Door) state).getFacing();
            //todo;
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 0.2);
        }), WOOD_DOOR, ACACIA_DOOR, BIRCH_DOOR, JUNGLE_DOOR, IRON_DOOR, DARK_OAK_DOOR, SPRUCE_DOOR),
        CARPET(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F),
                Material.CARPET),
        LILIPAD(new SimpleCollisionBox(0.0f, 0.0F, 0.0f, 1.0f, 0.015625F, 1.0f),
                Material.WATER_LILY),
        DEFAULT(new SimpleCollisionBox(0, 0, 0, 1, 1, 1));

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
        boolean isCollided(SimpleLocation location, CollisionBox other, Block block);
    }

    static class SimpleCollisionBox implements CollisionBox {
        double x1, y1, z1, x2, y2, z2;

        public SimpleCollisionBox() {
            this(0, 0, 0, 0, 0, 0);
        }

        public SimpleCollisionBox(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }

        public SimpleCollisionBox copy() {
            return new SimpleCollisionBox(x1, y1, z1, x2, y2, z2);
        }

        public SimpleCollisionBox offset(double x, double y, double z) {
            this.x1 += x;
            this.y1 += y;
            this.z1 += z;
            this.x2 += x;
            this.y2 += y;
            this.z2 += z;
            return this;
        }

        public SimpleCollisionBox offsetMin(double x, double y, double z) {
            this.x1 += x;
            this.y1 += y;
            this.z1 += z;
            return this;
        }

        public SimpleCollisionBox expandMax(double x, double y, double z) {
            this.x2 += x;
            this.y2 += y;
            this.z2 += z;
            return this;
        }

        public SimpleCollisionBox expand(double x, double y, double z) {
            this.x1 -= x;
            this.y1 -= y;
            this.z1 -= z;
            this.x2 += x;
            this.y2 += y;
            this.z2 += z;
            return this;
        }

        @Override
        public boolean isCollided(SimpleLocation location, CollisionBox other, Block block) {
            if (other instanceof SimpleCollisionBox) {
                SimpleCollisionBox box = ((SimpleCollisionBox) other);
                SimpleCollisionBox blockBox = copy().offset(block.getX(), block.getY(), block.getZ());
                boolean collided = (box.x2 > blockBox.x1 && box.x1 < blockBox.x2)
                        && ((box.y2 > blockBox.y1 && box.y1 < blockBox.y2)
                        && (box.z2 > blockBox.z1 && box.z1 < blockBox.z2));
                for (double x = blockBox.x1; x < blockBox.x2; x += 0.241) {
                    for (double y = blockBox.y1; y < (blockBox.y2 < 0.1 ? 0.1 : blockBox.y2); y += 0.241) {
                        for (double z = blockBox.z1; z < blockBox.z2; z += 0.241) {
                            final double fX = x;
                            final double fY = y;
                            final double fZ = z;
                            Bukkit.getOnlinePlayers().forEach(p -> {
                                TinyProtocolHandler.sendPacket(p, new PacketPlayOutWorldParticles(collided ? EnumParticle.FLAME : EnumParticle.CRIT, true, (float) fX, (float) fY, (float) fZ, 0F, 0F, 0F, 0, 1));
                            });
                        }
                    }
                }
                return collided;
            } else throw new IllegalStateException("Attempted to check collision with " + other.getClass().getSimpleName());
        }
    }

    static class ComplexCollisionBox implements CollisionBox {
        private List<CollisionBox> boxes = new ArrayList<>();

        public ComplexCollisionBox(CollisionBox... boxes) {
            Collections.addAll(this.boxes, boxes);
        }

        @Override
        public boolean isCollided(SimpleLocation location, CollisionBox other, Block block) {
            return boxes.stream().anyMatch(box -> box.isCollided(location, other, block));
        }
    }

    static class DynamicCollisionBox implements CollisionBox {
        private DynamicBox box;

        public DynamicCollisionBox(DynamicBox box) {
            this.box = box;
        }

        @Override
        public boolean isCollided(SimpleLocation location, CollisionBox other, Block block) {
            return box.fetch(block).isCollided(location, other, block);
        }

        interface DynamicBox {
            CollisionBox fetch(Block block);
        }
    }
}
