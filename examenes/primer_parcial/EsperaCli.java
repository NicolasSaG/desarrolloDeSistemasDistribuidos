import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class EsperaCli {
    public static void main(String[] args) {
        try {
            Socket conn = new Socket("localhost", 50000);

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            DataInputStream in = new DataInputStream(conn.getInputStream());

            // enviar conjuntos de doubles eficientemente
            ByteBuffer b;

            b = ByteBuffer.allocate(4 * 100);
            for (int i = 1; i < 200; i += 2) {
                b.putInt(i);
            }
            byte[] a = b.array();
            out.write(a);
            int res = in.readInt();
            System.out.println(res);

            out.close();
            in.close();
            conn.close();
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
