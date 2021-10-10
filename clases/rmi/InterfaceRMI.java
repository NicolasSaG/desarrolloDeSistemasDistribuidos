import java.rmi.Remote;
import java.rmi.RemoteException;

//estos metodos estan en otro dispositivo que se van a ejecutar de forma remota

public interface InterfaceRMI extends Remote {
    public String mayusculas(String name) throws RemoteException;

    public int suma(int a, int b) throws RemoteException;

    public long checksum(int[][] m) throws RemoteException;
}
