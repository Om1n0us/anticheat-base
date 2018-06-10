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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Check {
    private CheckType type;
    private Player player;
    // To make the code cleaner
    public PlayerData data;

    @Setter
    private int defaultViolations;
    private int defaultViolationTimeout;

    private List<Method> pre = new ArrayList<>();
    private List<Method> post = new ArrayList<>();

    public Check() {
        //TODO popular pre and post
    }

    public void call(Object argument) {
        Arrays.asList(pre, post).forEach(l -> {
            l.forEach(m -> {
                if (m.getParameterTypes()[0] == argument.getClass()) {
                    try {
                        m.invoke(this, argument);
                    } catch (Exception e) {
                        e.printStackTrace(); // todo some kind of exception logging to prevent spam
                    }
                }
            });
        });
    }

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
    public boolean fail() {
        return fail(defaultViolations, defaultViolationTimeout, null);
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
