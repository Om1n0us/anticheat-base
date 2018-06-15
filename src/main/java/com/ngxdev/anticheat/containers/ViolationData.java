/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.containers;

import com.ngxdev.anticheat.data.PlayerData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ViolationData {
    @NonNull public final PlayerData data;
    public List<Integer> violations = new ArrayList<>();
    private long lastTime;

    public int getViolation(long time) {
        violations.add(data.currentTick);
        violations.removeIf(l -> data.currentTick - l > time);
        lastTime = time;
        return violations.size();
    }

    public int getViolationOnly() {
        if (lastTime != 0) violations.removeIf(l -> data.currentTick - l > lastTime);
        return violations.size();
    }

    public void clearViolations() {
        violations.clear();
    }
}
