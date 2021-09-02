import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class ClienteMulticast {
    public static void main(String[] args) {

    }

    static void envia_mensaje(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress grupo = InetAddress.getByName(ip);
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, grupo, puerto);
        socket.send(paquete);
        socket.close();
    }
}
