package io.denery.packetutil;

/**
 *   A Super class for all packet types, has getters for ID and Data and
 *   simple parsing algorithm method
 */
public class Packet {
    private final byte id;
    private byte[] data = new byte[0];
    public Packet(byte id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    /**
     * Use to register packet in PacketBootstrap.
     *
     * @param id packet unique ID
     */
    public Packet(byte id) {
        this.id = id;
    }

    protected Packet(UtilPackets packets, byte[] data) {
        this.id = packets.getId();
        this.data = data;
    }

     /**
      *  Every packet has type identifier, it is a getter of identifier.
      *
      *  @return packet identifier.
      */
    public byte id() {
        return id;
    }

     /**
      *  Every packet has data in it, it is a getter of this data,
      *  except empty packets with only identifier.
      *
      *  @return the whole packet data including ID.
      *  @see #parsePacket() to get data without ID.
      */
    public byte[] getData() {
        return data;
    }

    /**
     *  Simple packet parser of a simple packet format.
     *
     *  @return byte array with data containing this packet
     *  without ID.
     */
    public byte[] parsePacket() {
        byte[] formattedData = new byte[data.length - 1];
        System.arraycopy(data, 0, formattedData, 0, formattedData.length);
        return formattedData;
    }
}
