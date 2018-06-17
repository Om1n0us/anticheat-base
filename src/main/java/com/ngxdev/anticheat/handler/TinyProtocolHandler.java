/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.BasePlugin;
import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.tinyprotocol.api.AbstractTinyProtocol;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutPosition;
import com.ngxdev.utils.Init;
import com.ngxdev.utils.exception.ExceptionLog;
import org.bukkit.entity.Player;

@Init
public class TinyProtocolHandler {
    private static AbstractTinyProtocol instance;

    // Purely for making the code cleaner
    public static void sendPacket(Player player, Object packet) {
        instance.sendPacket(player, packet);
    }

    public static int getProtocolVersion(Player player) {
        return instance.getProtocolVersion(player);
    }

    public TinyProtocolHandler() {
        TinyProtocolHandler self = this;
        instance = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8) ? new com.comphenix.tinyprotocol.v1_7_R4.TinyProtocol(BasePlugin.getInstance()) {
            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                return self.onPacketOutAsync(receiver, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                return self.onPacketInAsync(sender, packet);
            }
        } : new com.comphenix.tinyprotocol.v1_8_R3.TinyProtocol(BasePlugin.getInstance()) {
            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                return self.onPacketOutAsync(receiver, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                return self.onPacketInAsync(sender, packet);
            }
        };
    }

    public Object onPacketOutAsync(Player receiver, Object packet) {
        if (receiver == null) return packet;
        boolean cancel = false;
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);
        try {
            PlayerData data = PlayerData.get(receiver);
            switch (packetName) {
                case Packet.Server.POSITION: {
                    WrappedOutPosition wrapped = new WrappedOutPosition(packet);
                    wrapped.process(receiver, data.protocolVersion);
                    data.fireChecks(wrapped);
                    cancel = wrapped.isCancelled();
                    break;
                }
            }
        } catch (Exception e) {
            ExceptionLog.log(e);
        }
        return cancel ? null : packet;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
        if (sender == null) return packet;
        boolean cancel = false;
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);
        try {
            PlayerData data = PlayerData.get(sender);

            switch (packetName) {
                case Packet.Client.POSITION:
                case Packet.Client.LOOK:
                case Packet.Client.POSITION_LOOK:
                case Packet.Client.LEGACY_POSITION:
                case Packet.Client.LEGACY_LOOK:
                case Packet.Client.LEGACY_POSITION_LOOK:
                case Packet.Client.FLYING: {
                    WrappedInFlyingPacket wrapped = new WrappedInFlyingPacket(packet);
                    wrapped.process(sender, data.protocolVersion);
                    data.fireChecks(wrapped);
                    cancel = wrapped.isCancelled();
                    break;
                }
            }
        } catch (Exception e) {
            ExceptionLog.log(e);
        }
        return cancel ? null : packet;
    }
}
