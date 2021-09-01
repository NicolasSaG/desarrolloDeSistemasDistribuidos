import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;

class ServidorSSL {

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
        //System.setProperty("javax.net.debug", "all");

        try {
            ServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket servidor = socketFactory.createServerSocket(50000);
            // SSLServerSocket servidor = (SSLServerSocket)
            // socketFactory.createServerSocket(50000);
            Socket conexion = servidor.accept();
            // SSLSocket conexion = (SSLSocket) servidor.accept();
            DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
            DataInputStream in = new DataInputStream(conexion.getInputStream());

            double x = in.readDouble();
            System.out.println(x);

            out.close();
            in.close();
            conexion.close();
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            e.printStackTrace();
        }

    }
}
