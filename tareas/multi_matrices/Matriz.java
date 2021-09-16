import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

public class Matriz {
    static int n = 10;
    static long a[][] = new long[n][n];
    static long b[][] = new long[n][n];
    static long c[][] = new long[n][n];
    static Object obj = new Object();

    // ip publicas
    static String ipNodoCentral = "localhost";

    static class Worker extends Thread {
        Socket conexion;
        byte[] an;
        byte[] bn;

        Worker(Socket conexion, byte[] an, byte[] bn) {
            this.conexion = conexion;
            this.an = an;
            this.bn = bn;
        }

        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
                DataInputStream in = new DataInputStream(conexion.getInputStream());

                // enviar submatrices
                out.write(an);
                out.write(bn);
                // recibir multiplicacion de cn
                byte[] cn = new byte[(n / 4) * (n / 4) * 8];
                read(in, a, 0, (n / 4) * (n / 4) * 8);

                // guardar submatriz cn en matriz c
                out.close();
                in.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Error al ejecutar programa, agregue el nodo 0-4");
        }

        int nodo = Integer.parseInt(args[0]);

        if (nodo == 0) {
            inicializarMatrices();
            transponerMatriz(b);
            byte[] a1 = empaquetarSubMatriz(a, 0, n / 2, 0, n);
            byte[] a2 = empaquetarSubMatriz(a, n / 2, n, 0, n);
            byte[] b1 = empaquetarSubMatriz(b, 0, n / 2, 0, n);
            byte[] b2 = empaquetarSubMatriz(b, n / 2, n, 0, n);

            ServerSocket servidor;
            servidor = new ServerSocket(20000);
            Worker[] v = new Worker[4];

            // enviar submatrices

            // recibir submatrices

            // chance aqui va un join
            // generar matriz c

            // calcular checksum de matriz c
            long checksum = obtenerChecksum(c);
            System.out.println("Checksum matriz C: " + checksum);

            if (n == 10) {
                System.out.println("-----Matriz a -----");
                imprimirMatriz(a);
                System.out.println("----- Matriz b -----");
                imprimirMatriz(b);
                System.out.println("----- Matriz c -----");
                imprimirMatriz(c);
            }

            // long[][] aux_a1 = desempaquetarSubMatriz(a1, n, n / 2);
            // long[][] aux_a2 = desempaquetarSubMatriz(a2, n, n / 2);
            // long[][] aux_b1 = desempaquetarSubMatriz(b1, n, n / 2);
            // long[][] aux_b2 = desempaquetarSubMatriz(b2, n, n / 2);
            // System.out.println("a2");
            // imprimirMatriz(aux_a2);
            // System.out.println("b1");
            // imprimirMatriz(aux_b1);

            // long[][] c1 = multiplicarMatrices(aux_a1, aux_b1);
            // long[][] c2 = multiplicarMatrices(aux_a1, aux_b2);
            // long[][] c3 = multiplicarMatrices(aux_a2, aux_b1);
            // long[][] c4 = multiplicarMatrices(aux_a2, aux_b2);

            // checksum = 0;
            // checksum += obtenerChecksum(c1);
            // checksum += obtenerChecksum(c2);
            // checksum += obtenerChecksum(c3);
            // checksum += obtenerChecksum(c4);
        } else {
            Socket conexion = null;
            while (true) {
                try {
                    conexion = new Socket(ipNodoCentral, 20000);
                    break;
                } catch (Exception e) {
                    Thread.sleep(100);
                }
            }
            DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
            DataInputStream in = new DataInputStream(conexion.getInputStream());

            byte[] data = new byte[n * (n / 2) * 8];
            // recibir a enesimo
            read(in, data, 0, n * (n / 2) * 8);
            long[][] an = desempaquetarSubMatriz(data, n, n / 2);
            // recibir b enesimo
            read(in, data, 0, n * (n / 2) * 8);
            long[][] bn = desempaquetarSubMatriz(data, n, n / 2);

            long[][] cn = multiplicarMatrices(an, bn);
            byte[] cRes = empaquetarSubMatriz(cn, 0, cn.length, 0, cn[0].length);
            // enviar matriz
            out.write(cRes);

            out.close();
            in.close();
            conexion.close();
        }

    }

    private static long obtenerChecksum(long[][] matriz) {
        long res = 0;
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                res += matriz[i][j];
            }
        }
        return res;
    }

    private static void imprimirMatriz(long[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println("");
        }
    }

    private static long[][] multiplicarMatrices(long[][] m1, long[][] m2) {
        long res[][] = new long[m1.length][m2.length];
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m2.length; j++) {
                for (int k = 0; k < n; k++) {
                    res[i][j] += m1[i][k] * m2[j][k];
                }
            }
        }
        return res;
    }

    private static byte[] empaquetarSubMatriz(long[][] matriz, int low_i, int sup_i, int low_j, int sup_j) {
        ByteBuffer res = ByteBuffer.allocate((n * n / 2) * 8);

        for (int i = low_i; i < sup_i; i++) {
            for (int j = low_j; j < sup_j; j++) {
                res.putLong(matriz[i][j]);
            }
        }
        return res.array();
    }

    private static long[][] desempaquetarSubMatriz(byte[] data, int x, int y) {
        ByteBuffer b = ByteBuffer.wrap(data);
        long[][] res = new long[y][x];
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                res[i][j] = b.getLong();
            }
        }
        return res;
    }

    private static void transponerMatriz(long[][] b2) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < i; j++) {
                long x = b2[i][j];
                b2[i][j] = b2[j][i];
                b2[j][i] = x;
            }
    }

    private static void inicializarMatrices() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = 2 * i + j;
                b[i][j] = 2 * i - j;
            }
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
