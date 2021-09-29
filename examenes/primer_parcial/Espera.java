import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class Espera {
    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
                DataInputStream in = new DataInputStream(conexion.getInputStream());

                byte a[] = new byte[4 * 100];
                ByteBuffer b;
                read(in, a, 0, 4 * 100);
                b = ByteBuffer.wrap(a);
                int res = 0;
                for (int i = 0; i < 100; i++) {
                    res += b.getInt();
                }
                out.writeInt(res);

                out.close();
                in.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("ERROR:" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(50000);
        while (true) {
            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
        }
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }
}
