import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;

class ServidorSSL {

    public static void main(String[] args) throws Exception {
        ServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket servidor = socketFactory.createServerSocket(50000);

        Socket conexion = servidor.accept();
        DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
        DataInputStream in = new DataInputStream(conexion.getInputStream());

        System.out.println(in.readDouble());

        out.close();
        in.close();
        conexion.close();

    }
}
