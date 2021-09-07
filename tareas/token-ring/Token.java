import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Token {
    static DataInputStream entrada;
    static DataOutputStream salida;
    static boolean inicio = true;
    static String ip;
    static int nodo;
    static long token;

    static class Worker extends Thread {
        public void run() {
            // algoritmo 1
            try {
                ServerSocket servidor;
                servidor = new ServerSocket(20000 + nodo);
                Socket conexion;
                conexion = servidor.accept();
                entrada = new DataInputStream(conexion.getInputStream());

            } catch (Exception e) {
                System.out.println("Error:" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println(
                    "Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo en el anillo");
            System.exit(1);
        }

        nodo = Integer.valueOf(args[0]);
        ip = args[1];

        // Algoritmo 2
        Worker w;
        w = new Worker();
        w.start();
        Socket conexion = null;
        while (true) {
            try {
                conexion = new Socket(ip, 20000 + (nodo + 1) % 4);
                break;
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
        salida = new DataOutputStream(conexion.getOutputStream());
        w.join();
        while (true) {
            if (nodo == 0) {
                if (inicio) {
                    inicio = false;
                    token = 1;
                } else {
                    token = entrada.readLong();
                    token++;
                    System.out.println("Nodo: " + nodo + ", token = " + token);
                }
            } else {
                token = entrada.readLong();
                token++;
                System.out.println("Nodo: " + nodo + ", token = " + token);
            }

            if (nodo == 0 && token >= 1000) {
                break;
            }
            salida.writeLong(token);
        }
    }
}