package cliente_servidor;

import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        ServerSocket servidor = null;
        try {
            servidor = new ServerSocket(50000);
            Socket conexion = servidor.accept();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
