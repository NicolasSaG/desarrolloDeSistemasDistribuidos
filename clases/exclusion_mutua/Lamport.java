import java.io.DataOutputStream;
import java.net.Socket;

public class Lamport {
    static long reloj_logico;

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

    static class Reloj extends Thread {
        Reloj() {

        }

        public void run() {
            while (true) {
                System.out.println("reloj: " + reloj_logico);

            }
        }
    }
}
