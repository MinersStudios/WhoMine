package com.minersstudios.mscore.packet;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a packet registry for Minecraft packets.
 * This class maps packet classes to their corresponding
 * {@link PacketType} and vice versa.
 *
 * @see PacketType
 * @see PacketProtocol
 * @see PacketFlow
 * @see <a href="https://wiki.vg/Protocol">Protocol Wiki</a>
 */
public class PacketRegistry {
    private static final Map<Class<?>, PacketType> CLASS_TO_TYPE = new ConcurrentHashMap<>();
    private static final Map<PacketType, Class<?>> TYPE_TO_CLASS = new ConcurrentHashMap<>();

    static {
        ConnectionProtocol[] protocols = ConnectionProtocol.values();
        var serverMaps = new LinkedHashMap<ConnectionProtocol, Object2IntMap<?>>();
        var clientMaps = new LinkedHashMap<ConnectionProtocol, Object2IntMap<?>>();
        Field flowsField = null;

        for (var field : ConnectionProtocol.class.getDeclaredFields()) {
            if (
                    field.getType() == Map.class
                    && Modifier.isFinal(field.getModifiers())
                    && !Modifier.isStatic(field.getModifiers())
            ) {
                flowsField = field;
                flowsField.setAccessible(true);
                break;
            }
        }

        if (flowsField == null) {
            throw new RuntimeException("Could not find 'flows' field in ConnectionProtocol class");
        }

        for (var protocol : protocols) {
            Map<?, ?> flowsMap;

            try {
                flowsMap = (Map<?, ?>) flowsField.get(protocol);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access flows packet map", e);
            }

            for (var entry : flowsMap.entrySet()) {
                var flow = (PacketFlow) entry.getKey();
                var packetSet = entry.getValue();
                Field packetMapField = null;
                Object2IntMap<?> packetMap;

                for (var field : packetSet.getClass().getDeclaredFields()) {
                    if (
                            field.getType() == Object2IntMap.class
                            && Modifier.isFinal(field.getModifiers())
                            && !Modifier.isStatic(field.getModifiers())
                    ) {
                        packetMapField = field;
                        packetMapField.setAccessible(true);
                        break;
                    }
                }

                if (packetMapField == null) {
                    throw new RuntimeException("Could not find 'packetMap' field in packet set class");
                }

                try {
                    packetMap = (Object2IntMap<?>) packetMapField.get(packetSet);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to access packet map", e);
                }

                switch (flow) {
                    case CLIENTBOUND -> clientMaps.put(protocol, packetMap);
                    case SERVERBOUND -> serverMaps.put(protocol, packetMap);
                }
            }
        }

        for (var protocol : protocols) {
            PacketProtocol packetProtocol = PacketProtocol.fromMinecraft(protocol);

            if (serverMaps.containsKey(protocol)) {
                putPackets(serverMaps.get(protocol), packetProtocol, PacketFlow.SERVERBOUND);
            }

            if (clientMaps.containsKey(protocol)) {
                putPackets(clientMaps.get(protocol), packetProtocol, PacketFlow.CLIENTBOUND);
            }
        }
    }

    /**
     * Get an unmodifiable view of the map that maps
     * packet classes to their corresponding {@link PacketType}
     *
     * @return An unmodifiable view of the map containing
     *         packet classes as keys and their corresponding
     *         {@link PacketType} as values
     */
    public static @NotNull @UnmodifiableView Map<Class<?>, PacketType> getClassToType() {
        return Collections.unmodifiableMap(CLASS_TO_TYPE);
    }

    /**
     * Get an unmodifiable view of the map that maps
     * {@link PacketType} to their corresponding packet
     * classes
     *
     * @return An unmodifiable view of the map containing
     *         {@link PacketType} as keys and their corresponding
     *         packet classes as values
     */
    public static @NotNull @UnmodifiableView Map<PacketType, Class<?>> getTypeToClass() {
        return Collections.unmodifiableMap(TYPE_TO_CLASS);
    }

    /**
     * Get the {@link PacketType} associated with the given
     * packet class
     *
     * @param packet The packet class for which to
     *               retrieve the corresponding {@link PacketType}
     * @return The {@link PacketType} associated with the given
     *         packet class, or null if the packet class
     *         is not registered
     */
    public static @Nullable PacketType getTypeFromClass(@NotNull Class<?> packet) {
        if (packet == ClientboundBundlePacket.class) {
            return PacketType.Play.Client.BUNDLE_DELIMITER;
        }

        return CLASS_TO_TYPE.get(packet);
    }

    /**
     * Get the packet class associated with the given
     * {@link PacketType}
     *
     * @param type The {@link PacketType} for which to
     *             retrieve the corresponding packet class
     * @return The packet class associated with the given
     *         {@link PacketType}, or null if the PacketType
     *         is not registered
     */
    public static @NotNull Class<?> getClassFromType(@NotNull PacketType type) {
        return TYPE_TO_CLASS.get(type);
    }

    /**
     * Put the packets from the given map into the registry
     *
     * @param packetMap The map containing the packet classes
     * @param protocol  The protocol for which to register the packets
     * @param flow      The flow for which to register the packets
     */
    private static void putPackets(
            @NotNull Object2IntMap<?> packetMap,
            @NotNull PacketProtocol protocol,
            @NotNull PacketFlow flow
    ) {
        var map = protocol.getPackets().get(flow);

        for (var entry : packetMap.object2IntEntrySet()) {
            var packetType = map.get(entry.getIntValue());
            var packetClass = (Class<?>) entry.getKey();

            CLASS_TO_TYPE.put(packetClass, packetType);
            TYPE_TO_CLASS.put(packetType, packetClass);
        }
    }
}
