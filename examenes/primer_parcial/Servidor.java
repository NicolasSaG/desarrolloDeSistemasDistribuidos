
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Servidor {
    public static void main(String[] args) {
        ServerSocket servidor = null;
        try {
            servidor = new ServerSocket(50000);
            Socket conexion = servidor.accept();
            DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
            DataInputStream in = new DataInputStream(conexion.getInputStream());

            byte a[] = new byte[8 * 500];
            ByteBuffer b;
            read(in, a, 0, 8 * 500);
            b = ByteBuffer.wrap(a);
            double res = 0;
            for (int i = 0; i < 500; i++) {
                res += b.getDouble();
            }

            out.writeDouble(res);

            out.close();
            in.close();
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
