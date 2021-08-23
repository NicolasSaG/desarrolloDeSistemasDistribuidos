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

            int n = in.readInt();
            System.out.println("int recibido: " + n);
            double d = in.readDouble();
            System.out.println("double recibido: " + d);

            // recibir cadena
            byte[] buffer = new byte[4];
            read(in, buffer, 0, 4);
            System.out.println(new String(buffer, "UTF-8"));
            out.write("HOLA".getBytes());

            byte[] a = new byte[5 * 8];
            read(in, a, 0, 5 * 8);
            ByteBuffer b = ByteBuffer.wrap(a);
            for (int i = 0; i < 5; i++) {
                System.out.println("double recibido:" + b.getDouble());
            }

            // recibir 10k doubles
            int numeros = 10000;
            long inicio = System.currentTimeMillis();
            for (int i = 1; i <= numeros; i++) {
                d = in.readDouble();
                // System.out.println(d);
            }
            long total = System.currentTimeMillis() - inicio;
            System.out.println("tiempo en recibir " + numeros + " flotantes:" + total + " ms");

            a = new byte[8 * numeros];
            inicio = System.currentTimeMillis();
            read(in, a, 0, 8 * numeros);
            total = System.currentTimeMillis() - inicio;
            System.out.println("tiempo en recibir " + numeros + " flotantes:" + total + " ms");

            b = ByteBuffer.wrap(a);
            for (int i = 0; i < numeros; i++) {
                // System.out.println("double recibido:" + b.getDouble());
            }

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