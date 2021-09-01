import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileInputStream;

public class Cliente {

    public static void main(String[] args) throws Exception {
        // System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        // System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        // SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = null;
        while (true) {
            try {
                // conexion = cliente.createSocket("localhost", 50000);
                conexion = new Socket("localhost", 50000);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }

        DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
        DataInputStream in = new DataInputStream(conexion.getInputStream());

        // envio de datos
        String filename = args[0];
        byte[] data = lee_archivo(filename);
        out.writeUTF(filename);
        out.writeInt(data.length);
        out.write(data);

        // Thread.sleep(1000);
        out.close();
        in.close();
        conexion.close();
    }

    static byte[] lee_archivo(String archivo) throws Exception {
        FileInputStream f = new FileInputStream(archivo);
        byte[] buffer;
        try {
            buffer = new byte[f.available()];
            f.read(buffer);
        } finally {
            f.close();
        }
        return buffer;
    }
}
