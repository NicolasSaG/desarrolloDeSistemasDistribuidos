import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Ejercicio2 {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://sisdis.sytes.net:8080/Servicio/rest/ws/expresion");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String parametros = "a=" + URLEncoder.encode("b+(c&d)*(b=d)%c", "UTF-8") + "&" + "b="
                + URLEncoder.encode("45", "UTF-8") + "&" + "c=" + URLEncoder.encode("65", "UTF-8") + "&" + "d="
                + URLEncoder.encode("82", "UTF-8");
        OutputStream out = conexion.getOutputStream();
        out.write(parametros.getBytes());
        out.flush();

        if (conexion.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);

        }
        conexion.disconnect();
    }
}
