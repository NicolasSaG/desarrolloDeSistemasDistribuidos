import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//java Servidor2 0
class Servidor2 {
    static int coordinador_actual;
    static int nodo, num_nodos = 8;

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            String mensaje;
            try {
                DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
                DataInputStream in = new DataInputStream(conexion.getInputStream());
                mensaje = in.readUTF();
                if (mensaje.equals("ELECCION")) {
                    System.out.println("soy " + nodo + ": recibi mensaje de eleccion");
                    out.writeUTF("OK");
                    eleccion(nodo);
                } else if (mensaje.equals("COORDINADOR")) {
                    coordinador_actual = in.readInt();
                    System.out.println("recibiendo nuevo coordinador " + coordinador_actual);
                }

                out.close();
                in.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("ERROR:" + e.getMessage());
            }
        }
    }

    static class Servidor extends Thread {
        Servidor() {

        }

        public void run() {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(50000 + nodo);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            while (true) {
                try {
                    Socket socket = ss.accept();
                    Worker w = new Worker(socket);
                    w.start();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        nodo = Integer.parseInt(args[0]);
        Servidor s = new Servidor();
        s.start();

        // barrera
        System.out.println("ESPERANDO OTROS NODOS...");
        for (int i = 0; i < 8; i++) {
            if (nodo != i) {
                barrera("localhost", 50000 + i);
            }
        }
        System.out.println("INICIO");
        Thread.sleep(3000);
        if (nodo == 7) {
            System.exit(0);
        } else if (nodo == 4) {
            eleccion(nodo);
        }
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    static String barrera(String host, int puerto) throws Exception {
        String resultado = null;
        Socket cliente = null;
        while (true) {
            try {
                cliente = new Socket(host, puerto);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }

        DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
        out.writeUTF("BARRERA");

        out.close();
        cliente.close();
        return resultado;
    }

    static String envia_mensaje_eleccion(String host, int puerto) throws Exception {
        String resultado = null;
        Socket cliente = null;
        try {
            cliente = new Socket(host, puerto);
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
            DataInputStream in = new DataInputStream(cliente.getInputStream());

            out.writeUTF("ELECCION");
            resultado = in.readUTF();
            out.close();
            in.close();
        } catch (Exception e) {
            return "";
        } finally {
            if (cliente != null) {
                cliente.close();
            }
        }

        return resultado;
    }

    static void envia_mensaje_coordinador(String host, int puerto) throws Exception {
        Socket cliente = null;
        try {
            cliente = new Socket(host, puerto);
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
            DataInputStream in = new DataInputStream(cliente.getInputStream());

            out.writeUTF("COORDINADOR");
            out.writeInt(nodo);

            out.close();
            in.close();
        } catch (Exception e) {
            return;
        } finally {
            if (cliente != null) {
                cliente.close();
            }
        }

    }

    static void eleccion(int nodo) throws Exception {
        String respuesta;
        boolean bandera = false;
        for (int i = nodo + 1; i < num_nodos; i++) {
            System.out.println("enviando eleccion a " + i);
            respuesta = envia_mensaje_eleccion("localhost", 50000 + i);
            System.out.println("Respueta: " + respuesta);
            if (respuesta.equals("OK")) {
                bandera |= true;
            } else {
                bandera |= false;
            }
        }

        if (!bandera) {
            // nos convertimos en coordinador si nadie responde
            System.out.println("nadie me respondio, procedo a ser el nuevo coordinador");
            for (int i = 0; i < nodo; i++) {
                envia_mensaje_coordinador("localhost", 50000 + i);
            }
        }

    }
}
