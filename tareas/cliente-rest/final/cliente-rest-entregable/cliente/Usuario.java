/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fnico
 */
import com.google.gson.*;

public class Usuario {
    Integer id_usuario;
    String email;
    String nombre;

    String apellido_paterno;
    String apellido_materno;
    String fecha_nacimiento;
    String telefono;
    String genero;
    byte[] foto;

    // @FormParam necesita un metodo que convierta una String al objeto de tipo
    // Usuario
    public static Usuario valueOf(String s) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64()).create();
        return (Usuario) j.fromJson(s, Usuario.class);
    }
}
