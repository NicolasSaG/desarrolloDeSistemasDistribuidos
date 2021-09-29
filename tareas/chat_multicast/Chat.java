
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

//chcp 1252
public class Chat {
    static class Worker extends Thread {
        Worker() {
        }

        public void run() {
            System.setProperty("java.net.preferIPv4Stack", "true");
            try {
                MulticastSocket socket = new MulticastSocket(20000);
                InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"), 20000);
                NetworkInterface netInter = NetworkInterface.getByName("em1");// em0, bge0, bge1
                socket.joinGroup(grupo, netInter);
                while (true) {
                    byte[] a = recibe_mensaje_multicast(socket, 48);
                    System.out.println(new String(a, StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            // socket.leaveGroup(grupo, netInter);
            // socket.close();
        }
    }

    public static void main(String[] args) throws Exception {

        new Worker().start();
        String nombre = args[0];

        String mensaje = "";
        Scanner sc = new Scanner(System.in);

        // En un ciclo infinito se leerá cada mensaje del teclado y se enviará el
        // mensaje al grupo 230.0.0.0 a través del puerto 20000.
        System.setProperty("java.net.preferIPv4Stack", "true");

        while (true) {
            System.out.println("Ingrese el mensaje a enviar: ");
            mensaje = sc.nextLine();
            envia_mensaje_multicast((nombre + ":" + mensaje).getBytes(StandardCharsets.UTF_8), "230.0.0.0", 20000);
        }
    }

    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
}
