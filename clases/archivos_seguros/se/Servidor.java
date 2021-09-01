import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

class Servidor {
    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
                DataInputStream in = new DataInputStream(conexion.getInputStream());

                // recibir/enviar datos
                String filename = in.readUTF();
                int len = in.readInt();
                byte data[] = new byte[len];
                // recibir archivo
                read(in, data, 0, len);
                escribe_archivo(filename, data);

                // Thread.sleep(1000);
                out.close();
                in.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("ERROR:" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

        try {
            ServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket servidor = socketFactory.createServerSocket(50000);

            // ServerSocket servidor = new ServerSocket(50000);
            while (true) {
                Socket conexion = servidor.accept();
                Worker w = new Worker(conexion);
                w.start();
            }
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            e.printStackTrace();
        }

    }

    static void escribe_archivo(String archivo, byte[] buffer) throws Exception {
        FileOutputStream f = new FileOutputStream(archivo);
        try {
            f.write(buffer);
        } finally {
            f.close();
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
