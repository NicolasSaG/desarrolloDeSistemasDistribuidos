import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrizRMI extends UnicastRemoteObject implements InterfaceMatriz {
    public MatrizRMI() throws RemoteException {
        super();
    }

    public long[][] multiplicarMatrices(long[][] m1, long[][] m2, int n) throws RemoteException {
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
}
