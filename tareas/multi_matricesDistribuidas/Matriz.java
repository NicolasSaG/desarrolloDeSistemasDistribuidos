import java.rmi.RemoteException;
import java.rmi.Naming;

public class Matriz {
    static int n = 9;
    static double a[][] = new double[n][n];
    static double b[][] = new double[n][n];
    static double c[][] = new double[n][n];

    public static void main(String[] args) throws Exception {

        inicializarMatrices();
        if (n == 9) {
            System.out.println("a");
            imprimirMatriz(a);
            System.out.println("b");
            imprimirMatriz(b);
        }
        transponerMatriz(b);

        double[][] a1 = separa_matriz(a, 0);
        double[][] a2 = separa_matriz(a, n / 3);
        double[][] a3 = separa_matriz(a, (2 * n) / 3);

        double[][] b1 = separa_matriz(b, 0);
        double[][] b2 = separa_matriz(b, n / 3);
        double[][] b3 = separa_matriz(b, (2 * n) / 3);

        String url = "rmi://localhost/prueba";
        InterfaceMatriz r = (InterfaceMatriz) Naming.lookup(url);

        double[][] c1 = r.multiplicarMatrices(a1, b1, n);
        double[][] c2 = r.multiplicarMatrices(a1, b2, n);
        double[][] c3 = r.multiplicarMatrices(a1, b3, n);
        double[][] c4 = r.multiplicarMatrices(a2, b1, n);
        double[][] c5 = r.multiplicarMatrices(a2, b2, n);
        double[][] c6 = r.multiplicarMatrices(a2, b3, n);
        double[][] c7 = r.multiplicarMatrices(a3, b1, n);
        double[][] c8 = r.multiplicarMatrices(a3, b2, n);
        double[][] c9 = r.multiplicarMatrices(a3, b3, n);

        acomoda_matriz(c, c1, 0, 0);
        acomoda_matriz(c, c2, 0, n / 3);
        acomoda_matriz(c, c3, 0, (2 * n) / 3);
        acomoda_matriz(c, c4, n / 3, 0);
        acomoda_matriz(c, c5, n / 3, n / 3);
        acomoda_matriz(c, c6, n / 3, (2 * n) / 3);

        acomoda_matriz(c, c7, (2 * n) / 3, 0);
        acomoda_matriz(c, c8, (2 * n) / 3, n / 3);
        acomoda_matriz(c, c9, (2 * n) / 3, (2 * n) / 3);

        if (n == 9) {
            System.out.println("matriz c");
            imprimirMatriz(c);
        }

        System.out.println(obtenerChecksum(c));
    }

    private static double obtenerChecksum(double[][] matriz) {
        double res = 0;
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                res += matriz[i][j];
            }
        }
        return res;
    }

    private static void imprimirMatriz(double[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println("");
        }
    }

    private static void transponerMatriz(double[][] b2) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < i; j++) {
                double x = b2[i][j];
                b2[i][j] = b2[j][i];
                b2[j][i] = x;
            }
    }

    private static void inicializarMatrices() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = 4 * i + j;
                b[i][j] = 4 * i - j;
            }
        }
    }

    static double[][] separa_matriz(double[][] a, int renglon_inicial) {
        double[][] m = new double[n / 2][n];
        for (int i = 0; i < n / 3; i++) {
            for (int j = 0; j < n; j++) {
                m[i][j] = a[i + renglon_inicial][j];
            }
        }
        return m;
    }

    static void acomoda_matriz(double[][] C, double[][] c, int renglon, int columna) {
        for (int i = 0; i < n / 3; i++) {
            for (int j = 0; j < n / 3; j++) {
                C[i + renglon][j + columna] = c[i][j];
            }
        }
    }
}
