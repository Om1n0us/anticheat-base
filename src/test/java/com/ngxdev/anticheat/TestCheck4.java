/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Setting;
import com.ngxdev.anticheat.api.check.type.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

// Showcasing that values could be stored in checks
@CheckType(id = "check:test4", name = "Test Check4")
public class TestCheck4 extends Check {
    @Setting
    private int packetCount = 100;

    private int counter;

    // The actual check
    void movementChecker(WrappedInFlyingPacket packet) {
        if (counter++ == packetCount) {
            fail("You sent " + packetCount + " movement packets! Player, %s.", data.player.getName());
        }
    }
}
