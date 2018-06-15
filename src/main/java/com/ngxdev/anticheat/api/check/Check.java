/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import com.google.common.collect.Maps;
import com.ngxdev.anticheat.BasePlugin;
import com.ngxdev.anticheat.api.check.type.CheckType;
import com.ngxdev.anticheat.containers.ViolationData;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.utils.exception.ExceptionLog;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class Check {
    // Configurable values
    @Setting
    private double alertSensitivity = 1;
    @Setting
    private double cancelSensitivity = 1;
    @Setting
    private double banSensitivity = 1;

    private CheckType type;
    @Setter
    private Player player;
    // To make the code cleaner
    public PlayerData data;

    @Setter
    private int defaultViolations;
    @Setter
    private int defaultViolationTimeout;

    // Handles how violations work, feel free to modify this
    private ViolationData violationData;

    private LinkedList<Method> methods = new LinkedList<>(); // pre, post -> prioritized list

    public Check() {
        Map<Method, Byte> unsorted = new HashMap<>();

        for (Method method : getClass().getMethods()) {
            byte priority = Byte.MAX_VALUE;
            if (method.isAnnotationPresent(Priority.class)) {
                priority = method.getAnnotation(Priority.class).value();
            }
            unsorted.put(method, priority);
        }
        unsorted.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> methods.add(entry.getKey()));
        if (getClass().isAssignableFrom(CheckType.class)) {
            type = getClass().getAnnotation(CheckType.class);
            defaultViolations = type.maxVl();
            defaultViolationTimeout = type.timeout();
        }
    }

    public void init(PlayerData data) {
        this.player = data.player;
        this.data = data;
    }

    public void call(Object argument) {
        methods.forEach(m -> {
            if (m.getParameterTypes()[0] == argument.getClass()) {
                try {
                    m.invoke(this, argument);
                } catch (Exception e) {
                    ExceptionLog.log(e);
                }
            }
        });
    }

    /**
     * @param extra - Extra debug data, or values, like reach value
     * @param args  - Formatter args, "%s there" with arg "hello" will return "hello there"
     */
    public void debug(String extra, Object... args) {
        if (data.debug.check == this) {
            player.sendMessage(BasePlugin.getPrefix() + " §f" + type.name() + " §8/ §f" + String.format(extra, args));
        }
    }

    /**
     * @return if the player should be cancelled, pushed back, etc.
     */
    public boolean fail() {
        return fail(defaultViolations, defaultViolationTimeout, null);
    }

    /**
     * @return if the player should be cancelled, pushed back, etc.
     */
    public boolean fail(String extra, Object... args) {
        return fail(defaultViolations, defaultViolationTimeout, extra, args);
    }

    /**
     * @param violations       - the custom violations for the check to flag
     * @param violationTimeout - the custom time till violations expire
     * @param extra            - Extra debug data, or values, like reach value
     * @param args             - Formatter args, "%s there" with arg "hello" will return "hello there"
     * @return if the player should be cancelled, pushed back, etc.
     */
    public boolean fail(int violations, int violationTimeout, String extra, Object... args) {
        double vls = (double) violationData.getViolation(violationTimeout) / (double) violations;
        // Declared as fields for special occasions that would make it not be required to be alerted (generic lag check for example)
            boolean shouldAlert = type.alert();
            if (shouldAlert && vls >= alertSensitivity ) {
                player.sendMessage(BasePlugin.getPrefix() + " §f" + type.name() + " §8/ §f" + String.format(extra, args));
            }
        return false;
    }

    /**
     * Method wrapper
     */
    public void sendPacket(Packet packet) {
        sendPacket(packet.getPacket());
    }

    /**
     * Method wrapper
     */
    public void sendPacket(Object packet) {
        TinyProtocolHandler.sendPacket(player, packet);
    }
}
