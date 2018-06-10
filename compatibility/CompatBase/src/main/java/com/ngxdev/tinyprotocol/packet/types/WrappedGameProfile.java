/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.tinyprotocol.packet.types;

import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import com.ngxdev.tinyprotocol.reflection.Reflection;
import jdk.nashorn.internal.runtime.PropertyMap;
import lombok.Getter;

import java.util.UUID;

@Getter
public class WrappedGameProfile extends Packet {
    private static final String type = Type.GAMEPROFILE;

    // Fields
    private static FieldAccessor<UUID> fieldId = Reflection.getFieldSafe(type, UUID.class, 0);
    private static FieldAccessor<String> fieldName = Reflection.getFieldSafe(type, String.class, 0);
    private static FieldAccessor<?> fieldPropertyMap = Reflection.getFieldSafe(type, Reflection.getClass(Type.PROPERTYMAP), 0);

    // Decoded data
    public UUID id;
    public String name;
    public Object propertyMap;

    public WrappedGameProfile(Object type) {
        super(type);
    }

    @Override
    public void process(ProtocolVersion version) {
        id = fieldId.get(getPacket());
        name = fieldName.get(getPacket());
        propertyMap = fieldPropertyMap.get(getPacket());
    }
}