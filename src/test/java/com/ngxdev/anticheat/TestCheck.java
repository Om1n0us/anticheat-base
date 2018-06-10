/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import com.ngxdev.anticheat.api.check.*;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

// An actually decent speed check, does not account for being on ice, slabs, blocks above, etc.
@CheckType(id = "check:test", name = "Test Check")
public class TestCheck extends Check {

    // Parsing the flying packet into data
    // Parsers ALWAYS run before checker methods
    @Parser
    CustomEvent<WrappedInFlyingPacket> movementParser = event -> {
        data.movement.fx = data.movement.tx;
        data.movement.fy = data.movement.ty;
        data.movement.fz = data.movement.tz;
        data.movement.tx = event.getX();
        data.movement.ty = event.getY();
        data.movement.tz = event.getZ();
        data.movement.deltaH = Math.sqrt(Math.pow(data.movement.tx - data.movement.fx, 2.0) + Math.pow(data.movement.tz - data.movement.fz, 2.0));
        data.movement.deltaV = Math.abs(data.movement.fy - data.movement.ty);
    };

    // The actual check
    @Checker
    CustomEvent<WrappedInFlyingPacket> movementChecker = event -> {
        double limit = 0.3;

        if (data.movement.deltaH > limit);
    };
}
