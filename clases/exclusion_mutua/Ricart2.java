import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

//java Ricart2 0 localhost:50000 localhost:50001 localhost:50002
//java Ricart2 1 localhost:50000 localhost:50001 localhost:50002
//java Ricart2 2 localhost:50000 localhost:50001 localhost:50002
enum Recurso {
    Default, EnPosesion, EnEspera;
}

public class Ricart2 {
    static long reloj_logico;
    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;
    static Object lock = new Object();

    static LinkedList<Integer> cola = new LinkedList<Integer>();
    static int num_ok_recibidos;
    static long tiempo_logico_enviado;
    static Recurso estado = Recurso.Default;
    public static final int SINCRONIZAR_RELOJ = 0;
    public static final int SOLICITAR_RECURSO = 1;
    public static final int OK = 2;

    static class Worker extends Thread {
        Socket cliente;

        Worker(Socket cliente) {
            this.cliente = cliente;
        }

        public void run() {
            int peticion, nodo_recibido, id_recurso_recibido;
            long tiempo_recibido;
            try {
                DataInputStream in = new DataInputStream(cliente.getInputStream());
                peticion = in.readInt();

                if (peticion == SINCRONIZAR_RELOJ) {
                    tiempo_recibido = in.readLong();
                    synchronized (lock) {
                        if (tiempo_recibido >= reloj_logico) {
                            reloj_logico = tiempo_recibido + 1;
                        }
                    }
                } else if (peticion == SOLICITAR_RECURSO) {
                    nodo_recibido = in.readInt();
                    id_recurso_recibido = in.readInt();
                    tiempo_recibido = in.readLong();

                    System.out.println("\tnodo recibido: " + nodo_recibido);
                    System.out.println("\tid recurso recibido: " + id_recurso_recibido);
                    System.out.println("\ttiempo recibido: " + tiempo_recibido);
                    System.out.println("\testado actual: " + estado);

                    ajustar_reloj(tiempo_recibido);

                    if (estado == Recurso.EnPosesion) {
                        cola.add(nodo_recibido);
                    } else if (estado == Recurso.Default) {
                        long tiempo_temporal;
                        synchronized (lock) {
                            tiempo_temporal = reloj_logico;
                        }
                        envia_mensaje(OK, "OK", tiempo_temporal, hosts[nodo_recibido], puertos[nodo_recibido]);
                    } else if (estado == Recurso.EnEspera) {
                        long t1 = tiempo_recibido;
                        long t2 = tiempo_logico_enviado;
                        if (t1 < t2) {
                            long tiempo_temporal;
                            synchronized (lock) {
                                tiempo_temporal = reloj_logico;
                            }
                            envia_mensaje(OK, "OK", tiempo_temporal, hosts[nodo_recibido], puertos[nodo_recibido]);
                        } else if (t2 < t1) {
                            cola.add(nodo_recibido);
                        } else {
                            if (Math.min(nodo, nodo_recibido) == nodo) {
                                cola.add(nodo_recibido);
                            } else {
                                long tiempo_temporal;
                                synchronized (lock) {
                                    tiempo_temporal = reloj_logico;
                                }
                                envia_mensaje(OK, "OK", tiempo_temporal, hosts[nodo_recibido], puertos[nodo_recibido]);
                            }
                        }
                    }

                } else if (peticion == OK) {
                    String mensaje = in.readUTF();
                    tiempo_recibido = in.readLong();
                    ajustar_reloj(tiempo_recibido);

                    if (mensaje.equals("OK")) {
                        synchronized (lock) {
                            num_ok_recibidos += 1;
                            System.out.println("OK recibido..");
                            if (num_ok_recibidos == num_nodos - 1) {
                                System.out.println("adquiri el recurso.");
                                estado = Recurso.EnPosesion;
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

        for (int i = 0; i < num_nodos; i++) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.parseInt(args[i + 1].split(":")[1]);
        }

        Servidor servidor = new Servidor();
        servidor.start();

        // barrera para esperar a que todos los nodos esten on
        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                envia_mensaje(0, -1, hosts[i], puertos[i]);
            }
        }

        Reloj reloj = new Reloj();
        reloj.start();

        Thread.sleep(1000);
        bloquearRecurso();
        while (estado != Recurso.EnPosesion) {
            Thread.sleep(100);
        }
        Thread.sleep(3000);
        desbloquearRecurso();
    }

    private static void desbloquearRecurso() throws Exception {
        estado = Recurso.Default;
        int nodo_actual;
        long temp_tiempo;
        synchronized (lock) {
            temp_tiempo = reloj_logico;
        }

        while (!cola.isEmpty()) {
            nodo_actual = cola.poll();
            envia_mensaje(OK, "OK", temp_tiempo, hosts[nodo_actual], puertos[nodo_actual]);
        }
    }

    private static void bloquearRecurso() throws Exception {
        estado = Recurso.EnEspera;
        num_ok_recibidos = 0;
        synchronized (lock) {
            tiempo_logico_enviado = reloj_logico;
        }

        for (int i = 0; i < num_nodos; i++) {
            if (nodo != i) {
                envia_mensaje(SOLICITAR_RECURSO, nodo, 0, tiempo_logico_enviado, hosts[i], puertos[i]);
            }
        }
    }

    static void envia_mensaje(int peticion, int nodo, int id_recurso, long tiempo_logico, String host, int puerto)
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

        out.writeInt(peticion);
        out.writeInt(nodo);
        out.writeInt(id_recurso);
        out.writeLong(tiempo_logico);

        out.close();
        cliente.close();
    }

    static void envia_mensaje(int peticion, String mensaje, long tiempo_logico, String host, int puerto)
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

        out.writeInt(peticion);
        out.writeUTF(mensaje);
        out.writeLong(tiempo_logico);

        out.close();
        cliente.close();
    }

    static void envia_mensaje(int peticion, long tiempo_logico, String host, int puerto) throws Exception {
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
        synchronized (lock) {
            out.writeLong(tiempo_logico);
        }

        out.close();
        cliente.close();
    }

    static void ajustar_reloj(long tiempo_recibido) {
        synchronized (lock) {
            if (tiempo_recibido >= reloj_logico) {
                reloj_logico = tiempo_recibido + 1;
            }
        }
    }

}
