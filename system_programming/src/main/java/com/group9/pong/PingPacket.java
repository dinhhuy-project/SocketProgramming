package com.group9.pong;

import java.nio.ByteBuffer;

public class PingPacket extends PongPacket {
  public PingPacket(ByteBuffer buffer) {
    this.type = PacketType.PING;
  }

  @Override
  public byte[] toBytes() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'toBytes'");
  }
}
