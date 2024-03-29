/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.BasePlugin;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.anticheat.utils.PlayerTimer;
import com.ngxdev.utils.Init;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@Init
public class BukkitHandler implements Listener {
    @EventHandler
    public void onEvent(PlayerJoinEvent e) {
        PlayerData data = PlayerData.get(e.getPlayer());

        // We auto-initialize the fields to make PlayerData cleaner.
        for (Field f : data.getClass().getFields()) {
            try {
                if (!f.getType().isPrimitive() && f.get(data) == null) {
                    if (isDataClass(f.getType())) {
                        Object inst = f.getType().getConstructor(PlayerData.class).newInstance(data);
                        f.set(data, inst);
                        for (Field field : f.getType().getFields()) {
                            if (field.getType() == PlayerTimer.class) {
                                field.set(inst, new PlayerTimer(data));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        CheckHandler.init(data);
    }

    private static boolean isDataClass(Class clazz) {
        for (Constructor cons : clazz.getDeclaredConstructors()) {
            if (cons.getParameterCount() == 1 && cons.getParameterTypes()[0] == PlayerData.class) return true;
        }
        return false;
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        PlayerData data = PlayerData.playerData.remove(e.getPlayer());
        for (Check c : data.checks) {
            if (c instanceof Listener) HandlerList.unregisterAll((Listener) c);
        }
        // Help gc
        data.checks.clear();
        data.methods.clear();
        // Definition of: OO
        // OO(known as object orientated)
        //   * MEMORY LEAK
        //   * using a methodology which enables a system to be modelled as a set of objects which can be controlled and manipulated in a modular manner.
        Bukkit.getScheduler().runTaskLater(BasePlugin.getInstance(), () -> {
            PlayerData.playerData.remove(e.getPlayer());
        }, 20);
    }
}
