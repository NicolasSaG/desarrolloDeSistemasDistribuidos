import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

//java Ricart 0 localhost:50000 localhost:50001 localhost:50002
//java Ricart 1 localhost:50000 localhost:50001 localhost:50002
//java Ricart 2 localhost:50000 localhost:50001 localhost:50002
public class Ricart {
    static long reloj_logico;
    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;
    static Object lock = new Object();
    Queue<Integer> cola = new LinkedList<>();

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
                    if (tiempo_recibido >= reloj_logico) {
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
                        if (reloj_logico == 8) {
                            adquirir_recurso();
                            // esperar 3 segundos
                            liberar_recurso();
                        }

                    } else if (nodo == 1) {
                        reloj_logico += 5;
                        if (reloj_logico == 10) {
                            adquirir_recurso();
                            // esperar 3 segundos
                            liberar_recurso();
                        }
                    } else if (nodo == 2) {
                        reloj_logico += 6;
                        if (reloj_logico == 12) {
                            adquirir_recurso();
                            // esperar 3 segundos
                            liberar_recurso();
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
        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                envia_mensaje(-1, hosts[i], puertos[i]);
            }
        }

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

    static void envia_peticion(int id_recurso, int id_nodo, long tiempo_logico, String host, int puerto)
            throws Exception {
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
        out.writeInt(id_recurso);
        out.writeInt(id_nodo);
        out.writeLong(tiempo_logico);

        out.close();
        cliente.close();
    }

    static void adquirir_recurso() {
    }

    static void liberar_recurso() {
    }
}

// modificar envia mensaje para tipos de datos