/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import com.ngxdev.anticheat.BasePlugin;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.tinyprotocol.api.Packet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class Check {
    private CheckType type;
    private Player player;
    // To make the code cleaner
    public PlayerData data;

    @Setter
    private int defaultViolations;
    private int defaultViolationTimeout;

    /**
     * @param extra - Extra debug data, or values, like reach value
     * @param args - Formatter args, "%s there" with arg "hello" will return "hello there"
     * */
    public void debug(String extra, Object... args) {
        if (data.debug.check == this) {
            player.sendMessage(BasePlugin.getPrefix() + " §f" + type.name() + " §7=> §f" + String.format(extra, args));
        }
    }

    /**
     * @return if the player should be cancelled, pushed back, etc.
     * */
    public boolean fail(String extra, Object... args) {
        return fail(defaultViolations, defaultViolationTimeout, extra, args);
    }

    /**
     * @param violations - the custom violations for the check to flag
     * @param violationTimeout - the custom time till violations expire
     * @param extra - Extra debug data, or values, like reach value
     * @param args - Formatter args, "%s there" with arg "hello" will return "hello there"
     *
     * @return if the player should be cancelled, pushed back, etc.
     * */
    public boolean fail(int violations, int violationTimeout, String extra, Object... args) {
        return false;
    }

    /**
     * Method wrapper
     * */
    public void sendPacket(Packet packet) {
        sendPacket(packet.getPacket());
    }

    /**
     * Method wrapper
     * */
    public void sendPacket(Object packet) {
        TinyProtocolHandler.sendPacket(player, packet);
    }
}
