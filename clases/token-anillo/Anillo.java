import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class Anillo {
    static int nodo;
    static int num_nodos = 3;
    static DataInputStream in;
    static DataOutputStream out;

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            long token;

            try {
                out = new DataOutputStream(conexion.getOutputStream());
                in = new DataInputStream(conexion.getInputStream());

                token = in.readLong();
                System.out.println("Token recibido: " + token);
                out.writeLong(token);

                out.close();
                in.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("ERROR:" + e.getMessage());
            }
        }
    }

    static class Servidor extends Thread {
        Servidor() {

        }

        public void run() {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(50000 + nodo);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            while (true) {
                try {
                    Socket socket = ss.accept();
                    Worker w = new Worker(socket);
                    w.start();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        nodo = Integer.parseInt(args[0]);
        Servidor s = new Servidor();
        s.start();

    }
}
