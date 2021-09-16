import java.nio.ByteBuffer;

public class Matriz {
    static int n = 10;
    static long a[][] = new long[n][n];
    static long b[][] = new long[n][n];
    static long c[][] = new long[n][n];
    // ip publicas
    String ip_nodos[] = { "", "", "", "" };

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Error al ejecutar programa, agregue el nodo 0-4");
        }

        int nodo = Integer.parseInt(args[0]);

        switch (nodo) {
            case 0:
                inicializarMatrices();
                System.out.println("a");
                imprimirMatriz(a);
                System.out.println("b");
                imprimirMatriz(b);
                transponerMatriz(b);
                System.out.println("Transpuesta b");
                imprimirMatriz(b);
                byte[] a1 = empaquetarSubMatriz(a, 0, n / 2, 0, n);
                byte[] a2 = empaquetarSubMatriz(a, n / 2, n, 0, n);
                byte[] b1 = empaquetarSubMatriz(b, 0, n / 2, 0, n);
                byte[] b2 = empaquetarSubMatriz(b, n / 2, n, 0, n);

                // enviar submatrices

                // recibir submatrices

                // generar matriz c

                // imprimirMatriz(a);
                System.out.println("checksum a:" + obtenerChecksum(a));
                System.out.println("prueba de desempaque submatriz");
                long[][] aux_b1 = desempaquetarSubMatriz(b1, n, n / 2);
                imprimirMatriz(aux_b1);
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            default:
                System.out.println("El valor del nodo debe de estar entre 0 y 4");
                System.exit(0);
                break;
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
}
