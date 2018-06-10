/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.data;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.utils.PlayerTimer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Note, the only reason these don't have getters and setters to make the code cleaner in checks
@RequiredArgsConstructor
public class PlayerData {
    @NonNull public Player player;
    public int currentTick = 0;
    public List<Check> checks = new ArrayList<>();

    public class _velocity {
        public double fx, fy, fz; // Last location
        public double tx, ty, tz; // Current location
        public double deltaH;
        public double deltaV;
    }

    public class _movement {
        public double fx, fy, fz; // Last location
        public double tx, ty, tz; // Current location
        public double deltaH;
        public double deltaV;

        public boolean hasJumped, inAir;
    }

    public class _timers {
        public PlayerTimer lastJump;
    }

    public class _debug {
        public Check check;
    }


    // Ugly I know, these get automatically initialized
    public _velocity velocity;
    public _movement movement;
    public _timers timers;
    public _debug debug;

    // ------------------------------------------------ //
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
