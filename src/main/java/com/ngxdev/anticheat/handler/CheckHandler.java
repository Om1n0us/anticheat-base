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

import java.util.*;

@Init
public class CheckHandler {
    private static List<Class<? extends Check>> checks = new ArrayList<>();
    private static Map<Class<? extends Check>, CheckWrapper> wrappers = new HashMap<>();

    public CheckHandler() {
        Arrays.asList(ClassScanner.scanFile(CheckType.class.getName(), getClass()),
                ClassScanner.scanFile(NoOpCheck.class.getName(), getClass()))
                .forEach(list -> list.forEach(c -> {
                    try {
                        // Check casts
                        checks.add((Class<? extends Check>) Class.forName(c));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
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
    }
}
