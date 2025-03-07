package com.group9.pong;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class PongPacket {
  enum PacketType { PING, CONNECT, SYNC }

  public PacketType type;
  static Map<Integer, Function<ByteBuffer, PongPacket>>
  packets = new HashMap<>();

  private static void register(int packet_type, Function<ByteBuffer, PongPacket> constructor) {
    packets.put(packet_type, constructor);
  }

  static {
    register(PacketType.PING.ordinal(), PingPacket::new);
  }

  public abstract byte[] toBytes();
}
