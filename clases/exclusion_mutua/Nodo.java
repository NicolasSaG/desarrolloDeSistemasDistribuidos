import java.net.ServerSocket;
import java.net.Socket;

//parametros: numero_nodo_actual direcciones con puerto de los otros nodos
//java Nodo 0 localhost:50000 localhost:50001 localhost:50002
//java Nodo 1 localhost:50000 localhost:50001 localhost:50002
//java Nodo 2 localhost:50000 localhost:50001 localhost:50002
public class Nodo {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos;
    static int nodo;

    static class Worker extends Thread {
        Worker(Socket cliente) {

        }

        public void run() {
            System.out.println("Inicio del thread Worker");
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

    public static void main(String[] args) {
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
    }
}
