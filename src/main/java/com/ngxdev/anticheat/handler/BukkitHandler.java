/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.BasePlugin;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.anticheat.utils.PlayerTimer;
import com.ngxdev.utils.Init;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;

@Init
public class BukkitHandler implements Listener {
    @EventHandler
    public void onEvent(PlayerJoinEvent e) {
        PlayerData data = PlayerData.get(e.getPlayer());
        // We auto-initialize the fields to make PlayerData cleaner.
        for (Field f : data.getClass().getFields()) {
            try {
                if (!f.getType().isPrimitive() && f.get(data) != null) {
                    Object inst = f.getType().newInstance();
                    f.set(data, inst);
                    for (Field field : f.getType().getFields()) {
                        if (field.getType() == PlayerTimer.class) {
                            field.set(inst, new PlayerTimer(data));
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        PlayerData.playerData.remove(e.getPlayer());
        // Definition of: OO
        // OO(known as object orientated)
        //   * MEMORY LEAK
        //   * using a methodology which enables a system to be modelled as a set of objects which can be controlled and manipulated in a modular manner.
        Bukkit.getScheduler().runTaskLater(BasePlugin.getInstance(), () -> {
            PlayerData.playerData.remove(e.getPlayer());
        }, 20);
    }
}
