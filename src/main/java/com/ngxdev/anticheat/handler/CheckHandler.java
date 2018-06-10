/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.CheckType;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.utils.ClassScanner;
import com.ngxdev.utils.Init;

import java.util.ArrayList;
import java.util.List;

@Init
public class CheckHandler {
    private static List<Class<? extends Check>> checks = new ArrayList<>();

    public CheckHandler() {
        ClassScanner.scanFile(CheckType.class.getCanonicalName(), getClass()).forEach(c -> {
            try {
                // Check casts
                checks.add((Class<? extends Check>) Class.forName(c));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void init(PlayerData data) {
        checks.forEach(c -> {
            try {
                data.checks.add(c.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
