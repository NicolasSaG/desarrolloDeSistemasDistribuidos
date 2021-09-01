import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClienteSSL {

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        // System.setProperty("javax.net.debug", "all");

        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        // Socket conexion = cliente.createSocket("localhost", 50000);
        Socket conexion = null;
        // SSLSocket conexion = null;
        while (true) {
            try {
                conexion = cliente.createSocket("localhost", 50000);
                // conexion = (SSLSocket) cliente.createSocket("localhost", 50000);

                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }

        DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
        DataInputStream in = new DataInputStream(conexion.getInputStream());

        out.writeDouble(12312.234);
        Thread.sleep(1000);
        out.close();
        in.close();
        conexion.close();
    }
}
