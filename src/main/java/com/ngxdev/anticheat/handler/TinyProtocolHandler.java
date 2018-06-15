/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.data.PlayerData;
import com.ngxdev.tinyprotocol.api.AbstractTinyProtocol;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.utils.Init;
import com.ngxdev.utils.exception.ExceptionLog;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

    public TinyProtocolHandler(Plugin plugin) {
        TinyProtocolHandler self = this;
        instance = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8) ? new com.comphenix.tinyprotocol.v1_7_R4.TinyProtocol(plugin) {
            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                return self.onPacketOutAsync(receiver, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                return self.onPacketInAsync(sender, packet);
            }
        } : new com.comphenix.tinyprotocol.v1_8_R3.TinyProtocol(plugin) {
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
        boolean cancel = false;
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);
        try {
            PlayerData data = PlayerData.get(receiver);

            switch (packetName) {
                default: {

                }
            }
        } catch (Exception e) {
            ExceptionLog.log(e);
        }
        return cancel ? null : packet;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
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
                    wrapped.process(data.protocolVersion);
                    data.fireChecks(wrapped);
                    cancel = wrapped.isCancelled();
                }
            }
        } catch (Exception e) {
            ExceptionLog.log(e);
        }
        return cancel ? null : packet;
    }
}
