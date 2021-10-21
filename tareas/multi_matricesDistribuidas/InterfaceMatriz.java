import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceMatriz extends Remote {
    public double[][] multiplicarMatrices(double[][] m1, double[][] m2, int n) throws RemoteException;
}
