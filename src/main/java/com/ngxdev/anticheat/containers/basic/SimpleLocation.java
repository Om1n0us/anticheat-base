package com.ngxdev.anticheat.containers.basic;

import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SimpleLocation {
    double x, y, z;
    float yaw, pitch;

    public SimpleLocation(PlayerData data) {
        this.x = data.movement.tx;
        this.y = data.movement.ty;
        this.z = data.movement.tz;
        //this.yaw = data.movement.;
        //this.pitch = data.movement.tx;
    }

    public boolean equals(WrappedInFlyingPacket packet) {
        return x == packet.getX() && y == packet.getY() && z == packet.getZ() && yaw == packet.getYaw() && pitch == packet.getPitch();
    }
}
