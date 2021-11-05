import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import com.google.gson.*;

public class Cliente {
    private static String url_servidor;

    public static void main(String[] args) {
        menu();
    }

    private static void imprimirMenu() {
        System.out.println("MENU\n");
        System.out.println("a. Alta usuario");
        System.out.println("b. Consulta usuario");
        System.out.println("c. Borra usuario");
        System.out.println("d. Salir\n");
        System.out.print("Opción: ");
    }

    private static void menu() {
        Scanner sc = new Scanner(System.in, "UTF-8");
        String opcion = null;
        Usuario usuario = null;
        Integer id_usuario = null;
        do {
            imprimirMenu();
            opcion = sc.nextLine();

            switch (opcion) {
            case "a":
                usuario = obtenerDatosUsuario();
                alta_usuario(usuario);
                break;
            case "b":
                id_usuario = obtenerIdUsuario();
                usuario = consulta_usuario(id_usuario);
                if (usuario != null && obtenerRespuestaModificacion()) {
                    obtenerDatosModificacionUsuario(usuario);
                    modifica_usuario(usuario);
                }

                break;
            case "c":
                id_usuario = obtenerIdUsuario();
                borra_usuario(id_usuario);
                break;
            case "d":
                sc.close();
                System.exit(0);
                break;
            default:
                System.out.println("La opcion ingresada no coincide con las disponibles");
                break;
            }

        } while (!opcion.equals("d"));
    }

    private static boolean obtenerRespuestaModificacion() {
        Scanner sc = new Scanner(System.in);
        System.out.println("¿Desea modificar los datos del usuario (s/n)?");
        String respuesta = sc.nextLine();
        sc.close();
        if (respuesta.equals("s")) {
            return true;
        } else {
            return false;
        }
    }

    private static Usuario obtenerDatosUsuario() {
        Scanner sc = new Scanner(System.in, "UTF-8");
        Usuario usuario = new Usuario();
        System.out.print("email: ");
        usuario.email = sc.nextLine();
        System.out.print("nombre de usuario: ");
        usuario.nombre = sc.nextLine();
        System.out.print("apellido paterno: ");
        usuario.apellido_paterno = sc.nextLine();
        System.out.print("apellido materno: ");
        usuario.apellido_materno = sc.nextLine();
        System.out.print("fecha de nacimiento (dd/mm/aaaa): ");
        usuario.fecha_nacimiento = sc.nextLine();
        System.out.print("teléfono: ");
        usuario.telefono = sc.nextLine();
        System.out.print("género: ");
        usuario.genero = sc.nextLine();
        usuario.foto = null;
        sc.close();
        return usuario;
    }

    private static void obtenerDatosModificacionUsuario(Usuario usuario) {
        Scanner sc = new Scanner(System.in, "UTF-8");
        String temp_data = "";
        System.out.print("email: ");
        temp_data = sc.nextLine();
        if (temp_data.length() > 0) {
            usuario.email = temp_data;
        }
        System.out.print("nombre de usuario: ");
        temp_data = sc.nextLine();
        if (temp_data.length() > 0) {
            usuario.nombre = temp_data;
        }
        System.out.print("apellido paterno: ");
        temp_data = sc.nextLine();
        if (temp_data.length() > 0) {
            usuario.apellido_paterno = temp_data;
        }
        System.out.print("apellido materno: ");
        temp_data = sc.nextLine();
        if (temp_data.length() > 0) {
            usuario.apellido_materno = temp_data;
        }
        System.out.print("fecha de nacimiento (dd/mm/aaaa): ");
        temp_data = sc.nextLine();
        if (temp_data.length() > 0) {
            usuario.fecha_nacimiento = temp_data;
        }
        System.out.print("teléfono: ");
        temp_data = sc.nextLine();
        if (temp_data.length() > 0) {
            usuario.telefono = temp_data;
        }
        System.out.print("género: ");
        temp_data = sc.nextLine();
        if (temp_data.length() > 0) {
            usuario.genero = temp_data;
        }
        usuario.foto = null;
        sc.close();
        return usuario;
    }

    private static void imprimirDatosUsuario(Usuario usuario) {
        System.out.println("email: " + usuario.email);
        System.out.println("nombre de usuario:" + usuario.nombre);
        System.out.println("apellido paterno: " + usuario.apellido_paterno);
        System.out.println("apellido materno: " + usuario.apellido_materno);
        System.out.println("fecha nacimiento: " + usuario.fecha_nacimiento);
        System.out.println("telefono: " + usuario.telefono);
        System.out.println("genero: " + usuario.genero);

        System.out.println(respuesta);

    }

    private static Integer obtenerIdUsuario() {
        Integer id_usuario = null;
        Scanner sc = new Scanner(System.in, "UTF-8");
        System.out.print("id usuario: ");
        id_usuario = new Integer(sc.nextLine());
        sc.close();
        return id_usuario;
    }

    private static void alta_usuario(Usuario usuario) {
        URL url = new URL(url_servidor + "/Servicio/rest/wr/alta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        Gson j = new GsonBuilder().setDateFormat().create();

        String parametros = "usuario=" + URLEncoder.encode(j.toJson(usuario), "UTF-8");
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

    private static Usuario consulta_usuario(Integer id_usuario) {
        Usuario usuario = null;
        URL url = new URL(url_servidor + "/Servicio/rest/wr/consulta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String parametros = id_usuario + URLEncoder.encode(id_usuario.toString(), "UTF-8");

        OutputStream out = conexion.getOutputStream();
        out.write(parametros.getBytes());
        out.flush();

        if (conexion.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null) {
                System.out.println(respuesta);
                usuario = Usuario.valueOf(respuesta);
                imprimirDatosUsuario(usuario);
            }

        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            usuario = null;

        }
        conexion.disconnect();
        return usuario;
    }

    private static void modifica_usuario(Usuario usuario) {
        URL url = new URL(url_servidor + "/Servicio/rest/wr/modifica_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        Gson j = new GsonBuilder().setDateFormat().create();

        String parametros = "usuario=" + URLEncoder.encode(j.toJson(usuario), "UTF-8");
        OutputStream out = conexion.getOutputStream();
        out.write(parametros.getBytes());
        out.flush();

        if (conexion.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            System.out.println("el usuario ha sido modificado");
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);

        }
        conexion.disconnect();
    }

    private static void borra_usuario(Integer id_usuario) {
        URL url = new URL(url_servidor + "/Servicio/rest/wr/borra_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        Gson j = new GsonBuilder().setDateFormat().create();

        String parametros = "id_usuario=" + URLEncoder.encode(id_usuario.toString(), "UTF-8");
        OutputStream out = conexion.getOutputStream();
        out.write(parametros.getBytes());
        out.flush();

        if (conexion.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            System.out.println("el usuario ha sido borrado");
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);

        }
        conexion.disconnect();
    }
}