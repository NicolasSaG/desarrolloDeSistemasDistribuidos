import java.rmi.RemoteException;
import java.rmi.Naming;

public class MatrizLocal {
    static int n = 6;
    static long a[][] = new long[n][n];
    static long b[][] = new long[n][n];
    static long c[][] = new long[n][n];

    public static void main(String[] args) throws RemoteException, Exception {
        inicializarMatrices();
        System.out.println("a");
        imprimirMatriz(a);
        System.out.println("b");
        imprimirMatriz(b);
        System.out.println("b transpuesta");
        transponerMatriz(b);
        imprimirMatriz(b);

        String url = "rmi://localhost/prueba";
        InterfaceMatriz r = (InterfaceMatriz) Naming.lookup(url);

        long[][] a1 = separa_matriz(a, 0);
        long[][] a2 = separa_matriz(a, n / 2);
        long[][] b1 = separa_matriz(b, 0);
        long[][] b2 = separa_matriz(b, n / 2);

        // MatrizRMI o = new MatrizRMI();
        long[][] c1 = r.multiplicarMatrices(a1, b1, n);
        long[][] c2 = r.multiplicarMatrices(a1, b2, n);
        long[][] c3 = r.multiplicarMatrices(a2, b1, n);
        long[][] c4 = r.multiplicarMatrices(a2, b2, n);

        acomoda_matriz(c, c1, 0, 0);
        acomoda_matriz(c, c2, 0, n / 2);
        acomoda_matriz(c, c3, n / 2, 0);
        acomoda_matriz(c, c4, n / 2, n / 2);

        System.out.println("matriz c");
        imprimirMatriz(c);
        System.out.println(obtenerChecksum(c));
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
                a[i][j] = 2 * i - j;
                b[i][j] = i + 2 * j;
            }
        }
    }

    static long[][] separa_matriz(long[][] a, int renglon_inicial) {
        long[][] m = new long[n / 2][n];
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < n; j++) {
                m[i][j] = a[i + renglon_inicial][j];
            }
        }
        return m;
    }

    static void acomoda_matriz(long[][] C, long[][] c, int renglon, int columna) {
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < n / 2; j++) {
                C[i + renglon][j + columna] = c[i][j];
            }
        }
    }
}
