/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class BasePlugin extends JavaPlugin {
    @Getter
    private static String prefix = "§7[§fAntiCheat§7]";
    private static BasePlugin instance;

    public static BasePlugin getInstance() {
        return instance;
    }

    public BasePlugin() {
        instance = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
