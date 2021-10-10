import java.rmi.Naming;
import java.rmi.RemoteException;

public class ServidorRMI {
    public static void main(String[] args) throws Exception, RemoteException {
        // puerto 1099 creo
        System.setProperty("java.rmi.server.hostname", "localhost");
        String url = "rmi://localhost/prueba";
        ClaseRMI obj = new ClaseRMI();

        // registrar instancia en rmi registry
        Naming.rebind(url, obj);
    }
}
