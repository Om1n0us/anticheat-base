/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils;

import com.ngxdev.anticheat.data.PlayerData;

/**
 * A timer based on
 * */
public class PlayerTimer {
    private PlayerData player;
    public int startTime;

    public PlayerTimer(PlayerData player) {
        this.player = player;
        this.reset();
    }

    public boolean wasReset() {
        return this.startTime == player.currentTick;
    }

    public void reset() {
        this.startTime = player.currentTick;
    }

    public long getPassed() {
        return player.currentTick - this.startTime;
    }

    public void add(int amount) {
        this.startTime -= amount;
    }

    public boolean hasPassed(long toPass) {
        return this.getPassed() >= toPass;
    }

    public boolean hasNotPassed(long toPass) {
        return this.getPassed() < toPass;
    }

    public boolean hasPassed(long toPass, boolean reset) {
        boolean passed = this.getPassed() >= toPass;
        if (passed && reset) reset();
        return passed;
    }

    public static boolean hasPassed(long startTime, long toPass) {
        return (System.currentTimeMillis() - startTime) >= toPass;
    }
}
