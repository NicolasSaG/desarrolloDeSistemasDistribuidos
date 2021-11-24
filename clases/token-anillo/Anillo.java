import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class Anillo {
    static int nodo;
    static Object lock = new Object();
    static boolean bloqueo = false;
    static boolean solicitud_bloqueo = false;
    static int num_nodos = 3;

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            long token;

            try {
                DataInputStream in = new DataInputStream(conexion.getInputStream());
                token = in.readLong();
                // System.out.println("Token recibido: " + token);
                token += 1;
                synchronized (lock) {
                    System.out.println("solicitud_bloqueo " + solicitud_bloqueo);
                    if (solicitud_bloqueo) {
                        bloqueo = true;
                    } else {
                        bloqueo = false;
                    }
                    if (!bloqueo) {
                        bloqueo = false;
                        // System.out.println(" --" + solicitud_bloqueo);
                        envia_mensaje(token, "localhost", 50000 + (nodo + 1) % num_nodos);

                    }
                }

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

        if (nodo == 0) {
            envia_mensaje(1, "localhost", 50000 + (nodo + 1) % num_nodos);
        }
        Thread.sleep(3000);
        adquirirBloqueo();
        while (!bloqueo) {
            System.out.println("adquiriendo bloqueo...");
            Thread.sleep(300);
        }

        System.out.println("Bloqueo adquirido");
        Thread.sleep(3000);
        liberarBloqueo();
        System.out.println("Bloqueo liberado");
    }

    public static void adquirirBloqueo() {
        synchronized (lock) {
            solicitud_bloqueo = true;
        }
    }

    public static void liberarBloqueo() throws Exception {
        synchronized (lock) {
            solicitud_bloqueo = false;
        }
        // enviar token
        envia_mensaje(1, "localhost", 50000 + (nodo + 1) % num_nodos);
    }

    static void envia_mensaje(long token, String host, int puerto) throws Exception {
        Socket cliente = null;
        while (true) {
            try {
                cliente = new Socket(host, puerto);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }

        DataOutputStream out = new DataOutputStream(cliente.getOutputStream());

        out.writeLong(token);

        out.close();
        cliente.close();
    }
}
