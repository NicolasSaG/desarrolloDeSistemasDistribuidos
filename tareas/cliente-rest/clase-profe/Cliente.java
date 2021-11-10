import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Cliente {
    public static void main(String[] args) {
        URL url = new URL("ip-vm:8080/Servicio/rest/wr/consulta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String parametros = "email=" + URLEncoder.encode("nico@gmail.com", "UTF-8");
        // alta
        // usuario es instancia de Usuario, j es instacnia de lo de Gson
        // Gson j = new GsonBuilder().setDateFormat().create();
        // Usuario usuerio = new Usuario();
        // String parametros = "usuario=" + URLEncoder.encode(j.toJson(usuario),
        // "UTF-8");

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