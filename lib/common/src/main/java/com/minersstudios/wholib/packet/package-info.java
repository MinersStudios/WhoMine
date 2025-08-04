/**
 * This package contains classes for handling packets.
 * <p>
 * <table>
 *     <caption>Available Classes</caption>
 *     <tr>
 *         <th>Class</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>{@link com.minersstudios.wholib.packet.PacketContainer}</td>
 *         <td>Represents a packet container. It contains the packet and the
 *         packet type. The packet can be modified, but the packet type cannot
 *         be changed.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.minersstudios.wholib.packet.PacketEvent}</td>
 *         <td>Represents a packet event. It contains the packet container and
 *         the connection who sent or received the packet.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.minersstudios.wholib.packet.PacketListener}</td>
 *         <td>Represents a packet listener. It listens for packet events and
 *         handles them.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.minersstudios.wholib.packet.PacketRegistry}</td>
 *         <td>Represents a packet registry, which contains the packet maps.
 *         The packet registry contains two packet maps:
 *         <ul>
 *             <li>The path packet map, which maps the packet's path to the
 *             packet type</li>
 *             <li>The class packet map, which maps the packet's class to the
 *             packet type</li>
 *         </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.minersstudios.wholib.packet.PacketType}</td>
 *         <td>Represents a packet type used in the Minecraft server networking.
 *         It contains information about the packet's bound, its ID, and its
 *         resource key.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.minersstudios.wholib.packet.PacketBound}</td>
 *         <td>Represents a packet's bound in the Minecraft server networking.
 *         Available bounds are:
 *         <ul>
 *             <li>{@link com.minersstudios.wholib.packet.PacketBound#SERVERBOUND}
 *             - from the client to the server</li>
 *             <li>{@link com.minersstudios.wholib.packet.PacketBound#CLIENTBOUND}
 *             - from the server to the client</li>
 *         </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>{@link com.minersstudios.wholib.packet.PacketProtocol}</td>
 *         <td>Represents a packet protocol in the Minecraft server networking.
 *         Available protocols are:
 *         <ul>
 *             <li>{@link com.minersstudios.wholib.packet.PacketProtocol#HANDSHAKING}
 *             - the handshake protocol</li>
 *             <li>{@link com.minersstudios.wholib.packet.PacketProtocol#PLAY}
 *             - the play protocol</li>
 *             <li>{@link com.minersstudios.wholib.packet.PacketProtocol#STATUS}
 *             - the status protocol</li>
 *             <li>{@link com.minersstudios.wholib.packet.PacketProtocol#LOGIN}
 *             - the login protocol</li>
 *             <li>{@link com.minersstudios.wholib.packet.PacketProtocol#CONFIGURATION}
 *             - the configuration protocol</li>
 *          </ul>
 *          </td>
 *     </tr>
 * </table>
 *
 *
 * @see <a href="https://wiki.vg/Protocol">Protocol Wiki</a>
 * @see com.minersstudios.wholib.packet.registry Packet type registries
 * @see com.minersstudios.wholib.packet.collection Packet collections
 */
package com.minersstudios.wholib.packet;
