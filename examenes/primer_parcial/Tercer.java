import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Tercer {
    public static void main(String[] args) {
        try {
            Socket conn = new Socket("sisdis.sytes.net", 20000);

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            DataInputStream in = new DataInputStream(conn.getInputStream());

            // enviar datos
            out.writeInt(52);
            out.writeInt(80);
            out.writeDouble((double) 66);

            out.writeDouble((double) 81);

            double res = in.readDouble();
            System.out.println(res);
            // System.out.println(new String(buffer, "UTF-8"));
            // System.out.println(1632744980 -);
            // System.out.println(1632744980 -);
        } catch (Exception e) {
            // TODO: handle exception
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

/*
 * t1 = 1632744960 t2= 1632744980 t3= 1632744995 t4= 1632744998
 */
