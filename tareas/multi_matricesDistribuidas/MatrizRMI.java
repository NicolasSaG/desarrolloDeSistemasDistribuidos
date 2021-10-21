import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrizRMI extends UnicastRemoteObject implements InterfaceMatriz {
    public MatrizRMI() throws RemoteException {
        super();
    }

    public double[][] multiplicarMatrices(double[][] m1, double[][] m2, int n) throws RemoteException {
        double res[][] = new double[m1.length][m2.length];
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