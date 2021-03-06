import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Nodo {
    static int n = 10;
    static boolean[] b;
    static long[] m;

    // token-ring
    static int nodo;
    static int num_nodos = 4;
    static Object lock = new Object();
    static boolean bloqueo = false;
    static boolean solicitud_bloqueo = false;
    static String[] hosts;
    static int[] puertos;

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

                if (token == -2) { // actualizar
                    long num;
                    int pos;
                    num = in.readLong();
                    pos = in.readInt();
                    synchronized (lock) {
                        m[pos] = num;
                    }
                } else if (token != -1) {
                    // System.out.println("Token recibido: " + token);
                    token += 1;
                    synchronized (lock) {
                        // System.out.println("solicitud_bloqueo " + solicitud_bloqueo);
                        if (solicitud_bloqueo) {
                            bloqueo = true;
                        } else {
                            bloqueo = false;
                        }
                        if (!bloqueo) {
                            bloqueo = false;
                            // System.out.println(" --" + solicitud_bloqueo);
                            Thread.sleep(2000);
                            envia_mensaje(token, hosts[(nodo + 1) % num_nodos], puertos[(nodo + 1) % num_nodos]);
                        }
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
        hosts = new String[num_nodos];
        puertos = new int[num_nodos];

        for (int i = 0; i < num_nodos; i++) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.parseInt(args[i + 1].split(":")[1]);
        }

        m = new long[n];
        b = new boolean[n];

        Servidor s = new Servidor();
        s.start();

        // barrera
        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                envia_mensaje(-1, hosts[i], puertos[i]);
            }
        }
        System.out.println("todos los nodos activados");
        if (nodo == 0) {
            envia_mensaje(1, "localhost", 50000 + (nodo + 1) % num_nodos);
        }
        long r = 0;
        for (int i = 0; i < 1000; i++) {
            Lock();

            r = read(0);
            System.out.println(r);
            r++;
            write(0, r);
            Unlock();
        }
        if (nodo == 0) {
            System.out.println(r);
        }

    }

    private static long read(int n) {
        long res = -1;
        synchronized (lock) {
            res = m[n];
        }
        return res;
    }

    private static void write(int n, long value) {
        synchronized (lock) {
            m[n] = value;
            b[n] = true;
        }
    }

    public static void Lock() throws Exception {
        synchronized (lock) {
            solicitud_bloqueo = true;
        }
        for (int i = 0; i < b.length; i++) {
            b[i] = false;
        }
        while (!bloqueo) {
            // System.out.println("adquiriendo bloqueo...");
            Thread.sleep(100);
        }
    }

    public static void Unlock() throws Exception {
        // enviar cambios a los otros nodos
        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                for (int j = 0; j < b.length; j++) {
                    if (b[j]) {
                        // System.out.println("actualizando array del nodo " + i + ", posicion " + j +
                        // ", valor=" + m[j]);
                        actualiza_array(-2, m[j], j, hosts[i], puertos[i]);
                    }
                }
            }
        }

        synchronized (lock) {
            solicitud_bloqueo = false;
        }
        // enviar token
        envia_mensaje(1, "localhost", 50000 + (nodo + 1) % num_nodos);
    }

    private static void envia_mensaje(long token, String host, int puerto) throws Exception {
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

    private static void actualiza_array(long token, long cambio, int pos, String host, int puerto) throws Exception {
        Socket cliente = null;
        try {
            cliente = new Socket(host, puerto);
        } catch (Exception e) {
            Thread.sleep(100);
        }

        DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
        out.writeLong(token);
        out.writeLong(cambio);
        out.writeInt(pos);
        out.close();
        cliente.close();
    }
}