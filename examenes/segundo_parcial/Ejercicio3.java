import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Ejercicio3 {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://sisdis.sytes.net:8080/Servicio/rest/ws/calcular");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String parametros = "p1=" + URLEncoder.encode("56.6415", "UTF-8") + "&" + "p2="
                + URLEncoder.encode("93.862", "UTF-8") + "&" + "p3=" + URLEncoder.encode("93.985", "UTF-8") + "&"
                + "p4=" + URLEncoder.encode("21.4275", "UTF-8");
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
