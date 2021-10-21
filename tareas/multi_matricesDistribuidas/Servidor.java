import java.rmi.Naming;
import java.rmi.RemoteException;

public class Servidor {
    public static void main(String[] args) throws Exception, RemoteException {
        // puerto 1099 creo
        String url = "rmi://localhost/prueba";
        MatrizRMI obj = new MatrizRMI();

        // registrar instancia en rmi registry
        Naming.rebind(url, obj);
    }
}