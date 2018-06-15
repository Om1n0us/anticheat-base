/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import com.ngxdev.anticheat.api.check.*;
import com.ngxdev.anticheat.api.check.type.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

// Speed check, does not account for being on ice, slabs, blocks above, velocity, etc.
@CheckType(id = "check:test", name = "Test Check")
public class TestCheck extends Check {

    // Parsing the flying packet into data
    // Parsers ALWAYS run before checker methods
    // These can be put in seperate classes, doesn't really matter, use @NoOpCheck for that.
    @Priority(0)
    void movementParser(WrappedInFlyingPacket packet) {
        if (!packet.isPos()) return;
        data.currentTick++;

        data.movement.fx = data.movement.tx;
        data.movement.fy = data.movement.ty;
        data.movement.fz = data.movement.tz;
        data.movement.tx = packet.getX();
        data.movement.ty = packet.getY();
        data.movement.tz = packet.getZ();
        data.movement.deltaH = Math.sqrt(Math.pow(data.movement.tx - data.movement.fx, 2.0) + Math.pow(data.movement.tz - data.movement.fz, 2.0));
        data.movement.deltaV = Math.abs(data.movement.fy - data.movement.ty);

        if (data.movement.hasJumped) {
            data.movement.hasJumped = false;
            data.movement.inAir = true;
        }
        if (!data.movement.inAir && !packet.isGround() && data.movement.fy < data.movement.ty) {
            data.timers.lastJump.reset();
            data.movement.hasJumped = true;
        }
    }

    // The actual check
    void movementChecker(WrappedInFlyingPacket packet) {
        if (!packet.isPos()) return;
        double limit = 0.3;

        if (data.timers.lastJump.wasReset() || data.movement.hasJumped/*they both return the same value*/) {
            limit *= 1.5; //imaginary value
        }

        debug("%.5f", data.movement.deltaH);
        if (data.movement.deltaH > limit) {
            fail("%.2f(%s%%)", data.movement.deltaH, (int) ((limit / data.movement.deltaH) * 100));
        }
    }
}
