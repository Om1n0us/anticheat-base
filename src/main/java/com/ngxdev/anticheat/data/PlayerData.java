/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.data;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.MethodWrapper;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.anticheat.utils.PlayerTimer;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.utils.exception.ExceptionLog;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Note, the only reason these don't have getters and setters to make the code cleaner in checks
public class PlayerData {
    @NonNull
    public Player player;

    public int currentTick = 0;
    public ProtocolVersion protocolVersion;

    public List<Check> checks = new ArrayList<>();
    public LinkedList<MethodWrapper> methods = new LinkedList<>();


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
        public PlayerTimer lastTeleport;
        public PlayerTimer inUnloadedChunks;
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

    public void sortMethods() {
        LinkedList<MethodWrapper> sorted = new LinkedList<>();
        methods.stream()
                .sorted(Comparator.comparingInt(MethodWrapper::getPriority))
                .forEach(sorted::add);
        methods = sorted;
    }

    public void fireChecks(Object argument) {
        methods.forEach(m -> {
            try {
                m.call(argument);
            } catch (Exception e) {
                //System.out.println("Failed to call " + m.getMethod().getName());
                ExceptionLog.log(e);
            }
        });
    }
}
