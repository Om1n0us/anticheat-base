/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.CheckWrapper;
import com.ngxdev.anticheat.api.check.type.CheckType;
import com.ngxdev.anticheat.api.check.type.NoOpCheck;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.utils.ClassScanner;
import com.ngxdev.utils.Init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Init
public class CheckHandler {
    private static List<Class<? extends Check>> checks = new ArrayList<>();
    private static List<Class<? extends Check>> parsers = new ArrayList<>();
    private static Map<Class<? extends Check>, CheckWrapper> wrappers = new HashMap<>();

    public CheckHandler() {
        ClassScanner.scanFile(CheckType.class.getName(), getClass()).forEach(c -> {
            try {
                // Check casts
                Class<? extends Check> clazz = (Class<? extends Check>) Class.forName(c);
                checks.add(clazz);
                System.out.println("Registering check: " + clazz.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ClassScanner.scanFile(NoOpCheck.class.getName(), getClass()).forEach(c -> {
            try {
                // Check casts
                Class<? extends Check> clazz = (Class<? extends Check>) Class.forName(c);
                parsers.add(clazz);
                System.out.println("Registering parser: " + clazz.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void init(PlayerData data) {
        checks.forEach(c -> {
            try {
                Check check = c.newInstance();
                check.init(data, wrappers.computeIfAbsent(c, CheckWrapper::new));
                data.checks.add(check);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        parsers.forEach(c -> {
            try {
                Check check = c.newInstance();
                check.init(data, null);
                data.checks.add(check);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        data.sortMethods();
    }
}
