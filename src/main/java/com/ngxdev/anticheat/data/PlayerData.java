/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.data;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.anticheat.utils.PlayerTimer;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Note, the only reason these don't have getters and setters to make the code cleaner in checks
public class PlayerData {
    @NonNull public Player player;
    public int currentTick = 0;
    public List<Check> checks = new ArrayList<>();
    public ProtocolVersion protocolVersion;

    public PlayerData(Player player) {
        this.player = player;
        protocolVersion = ProtocolVersion.getVersion(TinyProtocolHandler.getProtocolVersion(player));
    }

    public class Velocity {
        public double fx, fy, fz; // Last location
        public double tx, ty, tz; // Current location
        public double deltaH;
        public double deltaV;
    }

    public class Movement {
        public double fx, fy, fz; // Last location
        public double tx, ty, tz; // Current location
        public double deltaH;
        public double deltaV;

        public boolean hasJumped, inAir;
    }

    public class Enviorment {
        public PlayerTimer
                onGround,
                onStairs, onSlabs,
                inWater, inLava, inWeb;
    }

    public class Timers {
        public PlayerTimer lastJump;
    }

    public class Debug {
        public Check check;
        public boolean debugMode;
    }


    // Ugly I know, these get automatically initialized
    public Velocity velocity;
    public Movement movement;
    public Enviorment enviorment;
    public Timers timers;
    public Debug debug;

    // Ease of use stuff
    public static final Map<Player, PlayerData> playerData = new ConcurrentHashMap<>();

    public static PlayerData get(Player player) {
        if (!player.isOnline()) return new PlayerData(player);
        else return playerData.computeIfAbsent(player, PlayerData::new);
    }

    public static Collection<PlayerData> getAll() {
        return playerData.values();
    }

    public void fireChecks(Object argument) {
        checks.forEach(c -> c.call(argument));
    }
}
