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
    static int count_ok;
    static boolean posesion_recurso = false;
    static boolean en_espera_recurso = false;
    static Object lock = new Object();
    static Queue<Integer> cola = new LinkedList<Integer>();
    static long[] tiempos_enviados;
    public static final int SINCRONIZAR_RELOJ = 0;
    public static final int SOLICITAR_RECURSO = 1;
    public static final int OK = 2;

    static class Worker extends Thread {
        Socket cliente;

        Worker(Socket cliente) {
            this.cliente = cliente;
        }

        public void run() {
            // System.out.println("Inicio del thread Worker");

            int peticion, nodo_recibido;
            long tiempo_recibido, t1, t2;
            String mensaje;
            try {
                DataInputStream in = new DataInputStream(cliente.getInputStream());
                peticion = in.readInt();

                if (peticion == SINCRONIZAR_RELOJ) {
                    nodo_recibido = in.readInt();
                    tiempo_recibido = in.readLong();
                    // implementar algoritmo de lamport
                    synchronized (lock) {
                        if (tiempo_recibido >= reloj_logico) {
                            reloj_logico = tiempo_recibido + 1;
                        }
                    }
                } else if (peticion == SOLICITAR_RECURSO) {
                    nodo_recibido = in.readInt();
                    tiempo_recibido = in.readLong();
                    System.out.println("nodo recibido: " + nodo_recibido);
                    synchronized (lock) {
                        System.out.println("peticion de solicitud de recurso del nodo " + nodo_recibido
                                + "\nEstado actual:\nEn posesion del recurso: " + posesion_recurso
                                + "\nEn espera del recurso:" + en_espera_recurso);

                        if (nodo_recibido == nodo) {
                            envia_mensaje(OK, "OK", hosts[nodo], puertos[nodo]);
                        } else if (posesion_recurso) {
                            cola.add(nodo_recibido);
                        } else if (!en_espera_recurso) {
                            envia_mensaje(OK, "OK", hosts[nodo_recibido], puertos[nodo_recibido]);
                        } else if (en_espera_recurso) {
                            t1 = tiempo_recibido;
                            t2 = tiempos_enviados[nodo_recibido];
                            System.out.println("t1 = " + t1 + " t2= " + t2);
                            if (t1 < t2) {
                                envia_mensaje(OK, "OK", hosts[nodo_recibido], puertos[nodo_recibido]);
                            } else if (t2 < t1) {
                                cola.add(nodo_recibido);
                            } else {
                                if (Math.min(nodo, nodo_recibido) == nodo) {
                                    cola.add(nodo_recibido);
                                } else {
                                    envia_mensaje(OK, "OK", hosts[nodo_recibido], puertos[nodo_recibido]);
                                }
                            }

                        }
                    }
                } else if (peticion == OK) {
                    mensaje = in.readUTF();
                    if (mensaje.equals("OK")) {
                        synchronized (lock) {
                            count_ok += 1;
                            if (count_ok == num_nodos) {
                                posesion_recurso = true;
                            }
                        }
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
        tiempos_enviados = new long[num_nodos];

        for (int i = 0; i < num_nodos; i++) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.parseInt(args[i + 1].split(":")[1]);
        }

        Servidor servidor = new Servidor();
        servidor.start();

        // barrera para esperar a que todos los nodos esten on
        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                envia_mensaje(SINCRONIZAR_RELOJ, i, -1, hosts[i], puertos[i]);
            }
        }

        Reloj reloj = new Reloj();
        reloj.start();

        // inicio de ricart
        synchronized (lock) {
            en_espera_recurso = true;
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (int i = 0; i < num_nodos; i++) {// bloquear recurso
            long temp;
            synchronized (lock) {
                temp = reloj_logico;
                System.out.println("Tiempo de envio:" + temp);
            }
            envia_mensaje(SOLICITAR_RECURSO, nodo, temp, hosts[i], puertos[i]);
            tiempos_enviados[i] = temp;

        }

        boolean flag = false;
        while (!flag) {
            synchronized (lock) {
                flag = posesion_recurso;
            }
        }
        System.out.println("recurso adquirido");
        synchronized (lock) {
            System.out.println("tiempo actual: " + reloj_logico);
            en_espera_recurso = false;
        }

        try {
            Thread.sleep(3000);
            synchronized (lock) {
                System.out.println("tiempoa despues de 3 segundos: " + reloj_logico);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        synchronized (lock) {
            posesion_recurso = false;
            System.out.println("Avisando a la cola en " + reloj_logico);
        }
        avisar_cola();
        servidor.join();

    }

    static void envia_mensaje(int peticion, int nodo, long tiempo_logico, String host, int puerto) throws Exception {
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

        out.writeInt(peticion);
        out.writeInt(nodo);
        out.writeLong(tiempo_logico);

        out.close();
        cliente.close();
    }

    static void envia_mensaje(int peticion, String mensaje, String host, int puerto) throws Exception {
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

        out.writeInt(peticion);
        out.writeUTF(mensaje);

        out.close();
        cliente.close();
    }

    static void avisar_cola() throws Exception {
        int nodo_actual;
        synchronized (lock) {
            while (!cola.isEmpty()) {
                nodo_actual = cola.poll();
                System.out.println("enviando OK a " + nodo_actual);
                envia_mensaje(OK, "OK", hosts[nodo_actual], puertos[nodo_actual]);
            }
        }
    }
}

// modificar envia mensaje para tipos de datos