package ws;

import javax.xml.ws.Endpoint;

//compilar en la carpeta padre de ws
//compilar javac ws/Servidor.java

//ejecutar: java ws/Servidor
public class Servidor {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/ServicioWeb", new ServicioWeb());
    }
}
