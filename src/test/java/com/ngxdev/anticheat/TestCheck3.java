/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.CheckType;
import com.ngxdev.anticheat.api.check.Checker;
import com.ngxdev.anticheat.api.check.NoOpCheck;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInVelocityPacket;

// Used only for parsing
@NoOpCheck
public class TestCheck3 extends Check {
    // The actual check
    @Checker
    void movementChecker(WrappedInVelocityPacket packet) {
        data.velocity.fx = data.velocity.tx;
        data.velocity.fy = data.velocity.ty;
        data.velocity.fz = data.velocity.tz;
        data.velocity.tx = packet.getX();
        data.velocity.ty = packet.getY();
        data.velocity.tz = packet.getZ();

        data.velocity.deltaH = Math.sqrt(Math.pow(data.velocity.tx - data.velocity.fx, 2.0) + Math.pow(data.velocity.tz - data.velocity.fz, 2.0));
        data.velocity.deltaV = Math.abs(data.velocity.fy - data.velocity.ty);
    }
}
