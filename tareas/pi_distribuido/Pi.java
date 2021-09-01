import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import jdk.jshell.spi.ExecutionControl.ExecutionControlException;

class Pi {
    static Object obj = new Object();
    static double pi = 0;

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            // algoritmo 1
            try {
                DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
                DataInputStream in = new DataInputStream(conexion.getInputStream());

                double suma = 0;
                // recibir suma del cliente
                suma = in.readDouble();

                synchronized (obj) {
                    pi += suma;
                }

                out.close();
                in.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Uso:");
            System.out.println("java PI <nodo>");
            System.exit(0);
        }

        int nodo = Integer.valueOf(args[0]);
        if (nodo == 0) {
            // servidor - algoritmo 2
            ServerSocket servidor;
            servidor = new ServerSocket(20000);
            Worker[] v = new Worker[4];

            int i = 0;
            while (i != 4) {
                Socket conexion;
                conexion = servidor.accept();
                v[i] = new Worker(conexion);
                v[i].start();
                i++;
            }

            i = 0;
            while (i != 4) {
                v[i].join();
                i++;
            }
            System.out.println("PI:" + pi);
            servidor.close();
        } else {
            // cliente - algoritmo 3
            // conexion con reintentos
            Socket conexion = null;
            while (true) {
                try {
                    conexion = new Socket("localhost", 20000);
                    break;
                } catch (Exception e) {
                    Thread.sleep(100);
                }
            }

            DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
            DataInputStream in = new DataInputStream(conexion.getInputStream());

            double suma = 0;
            int i = 0;
            while (i != 1000000) {
                suma = (4.0 / (8 * i + 2 * (nodo - 2) + 3)) + suma;
                i++;
            }
            suma = nodo % 2 == 0 ? -suma : suma;
            out.writeDouble(suma);
            out.close();
            in.close();
            conexion.close();
        }
    }
}