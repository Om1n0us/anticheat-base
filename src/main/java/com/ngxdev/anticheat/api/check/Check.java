/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import com.ngxdev.anticheat.BasePlugin;
import com.ngxdev.anticheat.api.check.type.CheckType;
import com.ngxdev.anticheat.containers.ViolationData;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.tinyprotocol.api.Packet;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;

@Getter
public class Check {
    // Static values
    private String id, name;
    private boolean experimental;

    // Configurable values
    private CheckWrapper check;

    public Player player;
    // To make the code cleaner
    public PlayerData data;

    // Handles how violations work, feel free to modify this
    private ViolationData violationData;

    public Check() {
        if (getClass().isAnnotationPresent(CheckType.class)) {
            CheckType type = getClass().getAnnotation(CheckType.class);
            this.id = type.id();
            this.name = type.name();
            this.experimental = type.experimental();
            if (isExperimental()) this.name = this.name + "*";
        }
    }

    public void init(PlayerData data, CheckWrapper wrapper) {
        this.player = data.player;
        this.data = data;
        this.check = wrapper;
        this.violationData = new ViolationData(data);

        initMethods();
    }

    private void initMethods() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (Arrays.asList("wait").contains(method.getName()) || method.getName().contains("lambda") || method.getParameterCount() != 1) continue;
            byte priority = Byte.MAX_VALUE;
            if (method.isAnnotationPresent(Priority.class)) {
                priority = method.getAnnotation(Priority.class).value();
            }
            method.setAccessible(true);
            data.methods.add(new MethodWrapper(this, method, priority));
        }
    }

    /**
     * @param extra - Extra debug data, or values, like reach value
     * @param args  - Formatter args, "%s there" with arg "hello" will return "hello there"
     */
    public void debug(String extra, Object... args) {
        if (data.debug.check == this || true) {
            player.sendMessage(BasePlugin.getPrefix() + " §f" + name + " §8/ §f" + String.format(extra, args));
        }
    }

    /**
     * @return if the player should be cancelled, pushed back, etc.
     */
    public boolean fail() {
        return fail(check.defaultViolations(), check.defaultViolationTimeout(), null);
    }

    /**
     * @return if the player should be cancelled, pushed back, etc.
     */
    public boolean fail(String extra, Object... args) {
        return fail(check.defaultViolations(), check.defaultViolationTimeout(), extra, args);
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
        boolean shouldAlert = check.alert();
        if (shouldAlert && vls >= check.alertSensitivity()) {
            player.sendMessage(BasePlugin.getPrefix() + " §f" + name + " §8/ §f" + String.format(extra, args));
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
