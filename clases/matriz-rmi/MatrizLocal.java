public class MatrizLocal {
    static int n = 6;
    static long a[][] = new long[n][n];
    static long b[][] = new long[n][n];
    static long c[][] = new long[n][n];

    public static void main(String[] args) {
        inicializarMatrices();
        System.out.println("a");
        imprimirMatriz(a);
        System.out.println("b");
        imprimirMatriz(b);
        System.out.println("b transpuesta");
        transponerMatriz(b);
        imprimirMatriz(b);

        long[][] a1 = separa_matriz(a, 0);
        long[][] a2 = separa_matriz(a, n / 2);
        long[][] b1 = separa_matriz(b, 0);
        long[][] b2 = separa_matriz(b, n / 2);

        long[][] c1 = multiplicarMatrices(a1, b1);
        long[][] c2 = multiplicarMatrices(a1, b2);
        long[][] c3 = multiplicarMatrices(a2, b1);
        long[][] c4 = multiplicarMatrices(a2, b2);

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
