package com.ngxdev.anticheat.parsers;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.anticheat.api.check.type.NoOpCheck;
import com.ngxdev.anticheat.containers.basic.SimpleLocation;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutPosition;
import com.ngxdev.utils.Utils;
import com.ngxdev.utils.evicting.EvictingList;
import com.ngxdev.anticheat.world.CollisionHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

import static com.ngxdev.anticheat.api.check.Priority.Value.NORMAL;
import static com.ngxdev.utils.Materials.*;

@NoOpCheck
public class MovementParser extends Check {
    private List<SimpleLocation> locations = new EvictingList<>(5);

    @Priority(NORMAL)
    void parse(WrappedInFlyingPacket packet) {
        if (!packet.isPos()) return;

        if (locations.stream().anyMatch(loc -> loc.equals(packet))) {
            data.movement.inAir = !packet.isGround();
            data.movement.hasJumped = false;
            data.movement.deltaH = 0;
            data.movement.deltaV = 0;
            data.timers.lastTeleport.reset();
            data.movement.fx = packet.getX();
            data.movement.fy = packet.getY();
            data.movement.fz = packet.getZ();
            data.movement.tx = packet.getX();
            data.movement.ty = packet.getY();
            data.movement.tz = packet.getZ();
            parseEnvironment();
            return;
        }

        data.currentTick++;

        data.movement.fx = data.movement.tx;
        data.movement.fy = data.movement.ty;
        data.movement.fz = data.movement.tz;
        data.movement.tx = packet.getX();
        data.movement.ty = packet.getY();
        data.movement.tz = packet.getZ();
        data.movement.deltaH = Math.hypot(data.movement.tx - data.movement.fx, data.movement.tz - data.movement.fz);
        data.movement.deltaV = Math.abs(data.movement.fy - data.movement.ty);

        if (data.movement.hasJumped) {
            data.movement.hasJumped = false;
            data.movement.inAir = true;
        }
        if (!data.movement.inAir && !packet.isGround() && data.movement.fy < data.movement.ty) {
            data.timers.lastJump.reset();
            data.movement.hasJumped = true;
        }
        parseEnvironment();
    }

    @Priority(NORMAL)
    void parse(WrappedOutPosition packet) {
        locations.add(new SimpleLocation(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch()));
    }

    private void parseEnvironment() {
        debug("-----------------------------------");
        World world = player.getWorld();

        int startX = Location.locToBlock(data.movement.tx - 0.31);
        int endX = Location.locToBlock(data.movement.tx + 0.31);
        int startY = Location.locToBlock(data.movement.ty -0.51);
        int endY = Location.locToBlock(data.movement.ty + 2.01);
        int startZ = Location.locToBlock(data.movement.tz - 0.31);
        int endZ = Location.locToBlock(data.movement.tz + 0.31);

        List<Block> blocks = new ArrayList<>();
        for (int bx = startX; bx <= endX; bx++) {
            for (int by = startY; by <= endY; by++) {
                for (int bz = startZ; bz <= endZ; bz++) {
                    Block block = getBlockAt(world, bx, by, bz);
                    if (block != null) {
                        if (block.getType() != Material.AIR) {
                            blocks.add(block);
                        }
                    } else data.timers.inUnloadedChunks.reset();
                }
            }
        }
        CollisionHandler handler = new CollisionHandler(blocks, new SimpleLocation(data));
        handler.setSize(0.6, 0.1);
        handler.setOffset(-0.01);
        if (handler.isCollidedWith(SOLID)) data.enviorment.onGround.reset();
        handler.setSize(0.6, 1.8);
        if (handler.isCollidedWith(LAVA)) data.enviorment.inLava.reset();
        if (handler.isCollidedWith(WATER)) data.enviorment.inWater.reset();
        debug("Chased: %s", Utils.join(blocks.stream().map(b -> b.getType().name()), ", "));
    }

    private Block getBlockAt(World world, int x, int y, int z) {
        if (world.isChunkLoaded(x >> 4, z >> 4)) return world.getChunkAt(x >> 4, z >> 4).getBlock(x & 15, y, z & 15);
        return null;
    }
}
