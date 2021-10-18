import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceMatriz extends Remote {
    public long[][] multiplicarMatrices(long[][] m1, long[][] m2, int n) throws RemoteException;
}
