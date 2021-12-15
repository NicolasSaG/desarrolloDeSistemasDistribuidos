import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Anillo {
    static int n = 10;
    static boolean[] b;
    static long[] m;

    // token
    static int nodo;
    static int num_nodos = 4;
    static Object lock = new Object();
    static boolean bloqueo = false;
    static boolean tengo_el_token = false;
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
                if (token == -1) {
                    // System.out.println("Barrera...");
                } else if (token == -2) {
                    // actualizacion de datos
                    long cambio = in.readLong();
                    int pos = in.readInt();
                    m[pos] = cambio;
                } else {
                    tengo_el_token = true;
                    // System.out.println(token);
                    if (!bloqueo)
                        envia_token(token);
                }

                in.close();
                conexion.close();
            } catch (

            Exception e) {
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
        for (int i = 0; i < m.length; i++) {
            m[i] = 0;
        }
        b = new boolean[n];

        Servidor s = new Servidor();
        s.start();

        // barrera
        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                envia_mensaje(-1, hosts[i], puertos[i]);
            }
        }
        // System.out.println("todos los nodos activados");
        if (nodo == 0) {
            envia_token(1);
        }
        long r = -1;
        for (int i = 0; i < 100; i++) {
            Lock();
            r = read(0);
            // System.out.println(r);
            r++;
            write(0, r);
            Unlock();
        }
        if (nodo == 0) {
            Lock();
            System.out.println(read(0));
            Unlock();
        }
        // Thread.sleep(5000);
        // Lock();
        // System.out.println("Adquiri el bloqueo");
        // Thread.sleep(5000);
        // Unlock();
        // System.out.println("libero bloqueo");
    }

    private static long read(int n) {
        long res = -1;
        res = m[n];
        return res;
    }

    private static void write(int n, long value) {
        m[n] = value;
        b[n] = true;
    }

    public static void Lock() throws Exception {
        for (int i = 0; i < b.length; i++) {
            b[i] = false;
        }

        synchronized (lock) {
            bloqueo = true;
        }

        while (!tengo_el_token) {
            Thread.sleep(100);
        }
    }

    public static void Unlock() throws Exception {
        // enviar cambios a los otros nodos
        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                for (int j = 0; j < b.length; j++) {
                    if (b[j]) {
                        actualiza_array(-2, m[j], j, hosts[i], puertos[i]);
                    }
                }
            }
        }

        // enviar token
        envia_token(1);
        synchronized (lock) {
            bloqueo = false;
        }
    }

    private static void envia_token(long token) throws Exception {
        String host = hosts[(nodo + 1) % num_nodos];
        int puerto = puertos[(nodo + 1) % num_nodos];
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
        tengo_el_token = false;
        out.close();
        cliente.close();
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