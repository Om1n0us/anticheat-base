/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.CheckType;
import com.ngxdev.anticheat.api.check.Checker;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

// Showcasing that values could be stored in checks
@CheckType(id = "check:test4", name = "Test Check4")
public class TestCheck4 extends Check {
    private int counter;

    // The actual check
    @Checker
    void movementChecker(WrappedInFlyingPacket packet) {
        if (counter++ == 100) {
            fail("You sent 100 movement packets!");
        }
    }
}
