import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//java Lamport 0 localhost:50000 localhost:50001 localhost:50002
//java Lamport 1 localhost:50000 localhost:50001 localhost:50002
//java Lamport 2 localhost:50000 localhost:50001 localhost:50002
public class Lamport {
    static long reloj_logico;
    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;
    static Object lock = new Object();

    static class Worker extends Thread {
        Socket cliente;

        Worker(Socket cliente) {
            this.cliente = cliente;
        }

        public void run() {
            System.out.println("Inicio del thread Worker");

            long tiempo_recibido;
            try {
                DataInputStream in = new DataInputStream(cliente.getInputStream());
                tiempo_recibido = in.readLong();

                // implementar algoritmo de lamport
                synchronized (lock) {
                    if (tiempo_recibido > reloj_logico) {
                        reloj_logico = tiempo_recibido + 1;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    static class Servidor extends Thread {
        Servidor() {

        }

        public void run() {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(puertos[nodo]);
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

    static class Reloj extends Thread {
        Reloj() {

        }

        public void run() {
            while (true) {
                synchronized (lock) {
                    System.out.println("reloj: " + reloj_logico);
                    if (nodo == 0) {
                        reloj_logico += 4;
                    } else if (nodo == 1) {
                        reloj_logico += 5;
                    } else if (nodo == 2) {
                        reloj_logico += 6;
                        if (reloj_logico == 102) {
                            try {
                                envia_mensaje(reloj_logico, hosts[1], puertos[1]);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        nodo = Integer.parseInt(args[0]);
        num_nodos = args.length - 1;
        hosts = new String[num_nodos];
        puertos = new int[num_nodos];

        for (int i = 0; i < num_nodos; i++) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.parseInt(args[i + 1].split(":")[1]);
        }

        Servidor servidor = new Servidor();
        servidor.start();
        // barrera para esperar a que todos los nodos esten on

        Reloj reloj = new Reloj();
        reloj.start();

        servidor.join();
    }

    static void envia_mensaje(long tiempo_logico, String host, int puerto) throws Exception {
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
        out.writeLong(tiempo_logico);

        out.close();
        cliente.close();
    }

}
