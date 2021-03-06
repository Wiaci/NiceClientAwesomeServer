import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class RequestReader {

    DatagramChannel channel;
    private static final Logger logger = LoggerFactory.getLogger(RequestReader.class);

    public RequestReader(DatagramChannel channel) {
        this.channel = channel;
    }

    public NiceToAwesomePacket getNewPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.clear();
        try {
            SocketAddress address;
            do {
                address = channel.receive(buffer);
            } while (address == null);
            logger.info("Запрос принят");
            NiceToAwesomePacket packet = deserialize(buffer.array());
            logger.info("Объект десериализован");
            packet.setSocketAddress(address);
            return packet;
        } catch (ClassNotFoundException e) {
            logger.warn("Класса"+ e.getCause() + "неееет");
        } catch (IOException e) {
            logger.warn("Вашему вниманию представляется стектрейс {}", e.getMessage());
        }
        return null;
    }

    public NiceToAwesomePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (NiceToAwesomePacket) inputStream.readObject();
    }

}
