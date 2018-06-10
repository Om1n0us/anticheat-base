/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.tinyprotocol.api.AbstractTinyProtocol;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TinyProtocolHandler {
    private static AbstractTinyProtocol instance;

    // Purely for making the code cleaner
    public static void sendPacket(Player player, Object packet) {
        instance.sendPacket(player, packet);
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

        return packet;
    }

    public Object onPacketInAsync(Player sender, Object packet) {

        return packet;
    }
}
