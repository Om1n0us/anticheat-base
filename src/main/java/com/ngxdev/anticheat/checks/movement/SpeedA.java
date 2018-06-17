package com.ngxdev.anticheat.checks.movement;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.anticheat.api.check.type.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static com.ngxdev.anticheat.api.check.type.CheckType.Type.MOVEMENT;

@CheckType(id = "speed:a", name = "Speed A", type = MOVEMENT, experimental = true)
public class SpeedA extends Check {
    void check(WrappedInFlyingPacket packet) {
        if ((data.movement.deltaH < 0.001 && data.movement.deltaV < 0.001) || !packet.isPos()) return;
        debug("H: %.3f, V: %.3f", data.movement.deltaH, data.movement.deltaV);
    }
}
