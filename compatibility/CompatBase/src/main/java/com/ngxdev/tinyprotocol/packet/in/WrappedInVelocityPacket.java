/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.tinyprotocol.packet.in;

import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import com.ngxdev.tinyprotocol.reflection.Reflection;
import lombok.Getter;

@Getter
public class WrappedInVelocityPacket extends Packet {
    private static final String packet = Client.FLYING;

    // Fields
    private static FieldAccessor<Double> fieldX = Reflection.getFieldSafe(packet, double.class, 0);
    private static FieldAccessor<Double> fieldY = Reflection.getFieldSafe(packet, double.class, 1);
    private static FieldAccessor<Double> fieldZ = Reflection.getFieldSafe(packet, double.class, 2);
    private static FieldAccessor<Float> fieldYaw = Reflection.getFieldSafe(packet, float.class, 0);
    private static FieldAccessor<Float> fieldPitch = Reflection.getFieldSafe(packet, float.class, 1);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;
    private boolean look, pos;

    public WrappedInVelocityPacket(Object packet) {
        super(packet);
    }

    @Override
    public void process(ProtocolVersion version) {
        String name = getPacketName();
        // This saves up 2 reflection calls
        if (version.isBelow(ProtocolVersion.V1_8)) {
            pos = name.equals(Client.LEGACY_POSITION) || name.equals(Client.LEGACY_POSITION_LOOK);
            look = name.equals(Client.LEGACY_LOOK) || name.equals(Client.LEGACY_POSITION_LOOK);
        } else {
            pos = name.equals(Client.POSITION) || name.equals(Client.POSITION_LOOK);
            look = name.equals(Client.LOOK) || name.equals(Client.POSITION_LOOK);
        }
        x = fieldX.get(getPacket());
        y = fieldY.get(getPacket());
        z = fieldZ.get(getPacket());
        yaw = fieldYaw.get(getPacket());
        pitch = fieldPitch.get(getPacket());
    }
}
