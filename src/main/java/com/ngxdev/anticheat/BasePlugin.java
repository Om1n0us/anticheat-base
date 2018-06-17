/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import com.ngxdev.utils.ClassScanner;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BasePlugin extends JavaPlugin {
    @Getter
    private static String prefix = "§fBasePlugin §8//";
    private static BasePlugin instance;

    public static BasePlugin getInstance() {
        return instance;
    }

    public BasePlugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        ClassScanner.scanFile(null, getClass()).forEach(c -> {
            try {
                Class clazz = Class.forName(c);
                System.out.println("Initializing class: " + clazz.getSimpleName());
                Object obj = clazz.newInstance();
                if (obj instanceof Listener) {
                    Bukkit.getPluginManager().registerEvents((Listener) obj, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
