package ws;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
//compilar en la carpeta padre de ws
//compilar javac ws/ServicioWeb.java

@WebService
public class ServicioWeb {
    @WebMethod
    public double suma(@WebParam(name = "a") double a, @WebParam(name = "b") double b) {
        return a + b;
    }

    @WebMethod
    public String mayusculas(@WebParam(name = "s") String s) {
        return s.toLowerCase();
    }

    @WebMethod
    public List<Integer> suma2(@WebParam(name = "a") List<Integer> a, @WebParam(name = "b") List<Integer> b) {
        List<Integer> c = new ArrayList<Integer>();
        for (int i = 0; i < a.size(); i++) {
            c.add(a.get(i) + b.get(i));
        }
        return c;
    }
}