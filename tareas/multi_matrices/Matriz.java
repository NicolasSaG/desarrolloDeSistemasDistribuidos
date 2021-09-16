import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Matriz {
    static int n = 10;
    static long a[][] = new long[n][n];
    static long b[][] = new long[n][n];
    static long c[][] = new long[n][n];
    static Object obj = new Object();
    static byte[] a1;
    static byte[] a2;
    static byte[] b1;
    static byte[] b2;

    // ip publicas
    static String ipNodoCentral = "13.72.77.247";

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(conexion.getOutputStream());
                DataInputStream in = new DataInputStream(conexion.getInputStream());
                // identificar al nodo
                int idNodo = in.readInt();

                // enviar submatrices
                if (idNodo == 1) {
                    out.write(a1);
                    out.write(b1);
                } else if (idNodo == 2) {
                    out.write(a1);
                    out.write(b2);
                } else if (idNodo == 3) {
                    out.write(a2);
                    out.write(b1);
                } else if (idNodo == 4) {
                    out.write(a2);
                    out.write(b2);
                }

                // recibir multiplicacion de cn
                byte[] cn = new byte[(n / 2) * (n / 2) * 8];
                read(in, cn, 0, (n / 2) * (n / 2) * 8);
                long[][] cn_matriz = desempaquetarSubMatriz(cn, n / 2, n / 2);

                // guardar submatriz cn en matriz c
                synchronized (obj) {
                    if (idNodo == 1) {
                        for (int i = 0; i < n / 2; i++) {
                            for (int j = 0; j < n / 2; j++) {
                                c[i][j] = cn_matriz[i][j];
                            }
                        }
                    } else if (idNodo == 2) {
                        for (int i = 0; i < n / 2; i++) {
                            for (int j = 0; j < n / 2; j++) {
                                c[i][n / 2 + j] = cn_matriz[i][j];
                            }
                        }
                    } else if (idNodo == 3) {
                        for (int i = 0; i < n / 2; i++) {
                            for (int j = 0; j < n / 2; j++) {
                                c[n / 2 + i][j] = cn_matriz[i][j];
                            }
                        }
                    } else if (idNodo == 4) {
                        for (int i = 0; i < n / 2; i++) {
                            for (int j = 0; j < n / 2; j++) {
                                c[n / 2 + i][n / 2 + j] = cn_matriz[i][j];
                            }
                        }
                    }
                }

                out.close();
                in.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Error al ejecutar programa, agregue el nodo 0-4");
        }

        int nodo = Integer.parseInt(args[0]);

        if (nodo == 0) {
            inicializarMatrices();
            transponerMatriz(b);
            a1 = empaquetarSubMatriz(a, 0, n / 2, 0, n);
            a2 = empaquetarSubMatriz(a, n / 2, n, 0, n);
            b1 = empaquetarSubMatriz(b, 0, n / 2, 0, n);
            b2 = empaquetarSubMatriz(b, n / 2, n, 0, n);

            ServerSocket servidor;
            servidor = new ServerSocket(20000);
            Worker[] v = new Worker[4];
            int i = 0;
            while (i != 4) {
                Socket conexion;
                conexion = servidor.accept();
                v[i] = new Worker(conexion);

                v[i].start();
                i++;
            }

            i = 0;
            while (i != 4) {
                v[i].join();
                i++;
            }
            servidor.close();

            // calcular checksum de matriz c
            long checksum = obtenerChecksum(c);
            System.out.println("Checksum matriz C: " + checksum);

            if (n == 10) {
                System.out.println("-----Matriz a -----");
                imprimirMatriz(a);
                System.out.println("----- Matriz b -----");
                transponerMatriz(b);
                imprimirMatriz(b);
                System.out.println("----- Matriz c -----");
                imprimirMatriz(c);
            }
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

            // enviar idNodo
            out.writeInt(nodo);

            byte[] data = new byte[n * (n / 2) * 8];
            // recibir a enesimo
            read(in, data, 0, n * (n / 2) * 8);
            long[][] an = desempaquetarSubMatriz(data, n, n / 2);

            // recibir b enesimo
            read(in, data, 0, n * (n / 2) * 8);
            long[][] bn = desempaquetarSubMatriz(data, n, n / 2);
            imprimirMatriz(bn);
            long[][] cn = multiplicarMatrices(an, bn);
            imprimirMatriz(cn);
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
        ByteBuffer res = ByteBuffer.allocate(((sup_i - low_i) * (sup_j - low_j)) * 8);

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
                a[i][j] = i + 2 * j;
                b[i][j] = i - 2 * j;
                // a[i][j] = 2 * i + j;
                // b[i][j] = 2 * i - j;
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
