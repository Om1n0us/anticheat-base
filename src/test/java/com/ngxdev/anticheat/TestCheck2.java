/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.CheckType;
import com.ngxdev.anticheat.api.check.Checker;
import com.ngxdev.anticheat.api.check.Parser;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

// Showcasing that values could be used from other checks
@CheckType(id = "check:test2", name = "Test Check2")
public class TestCheck2 extends Check {
    // The actual check
    @Checker
    void movementChecker(WrappedInFlyingPacket packet) {
        if (!packet.isPos()) return;

        if (data.movement.deltaH > 4) {
            fail("Sonic The Hedgehog");
        }
    };
}
