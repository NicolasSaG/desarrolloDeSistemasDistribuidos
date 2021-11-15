package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import com.google.gson.*;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio

@Path("ws")
public class Servicio {
    static DataSource pool = null;
    static {
        try {
            Context ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

    @POST
    @Path("alta_articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alta(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();

        if (articulo.descripcion == null || articulo.descripcion.equals(""))
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una descripcion para el artículo")))
                    .build();

        try {
            PreparedStatement stmt_1 = conexion
                    .prepareStatement("select id_articulo from articulos where descripcion=?");
            try {
                stmt_1.setString(1, articulo.descripcion);
                ResultSet rs = stmt_1.executeQuery();
                try {
                    if (rs.next())
                        return Response.status(400)
                                .entity(j.toJson(new Error("ya hay un articulo con esa descripcion"))).build();
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }

            // alta
            PreparedStatement stmt_2 = conexion.prepareStatement("insert into articulos VALUES (0,?,?,?)");
            try {
                stmt_2.setString(1, articulo.descripcion);
                stmt_2.setFloat(2, articulo.precio);
                stmt_2.setInt(3, articulo.cantidad);
                stmt_2.executeUpdate();
            } finally {
                stmt_2.close();
            }

            if (articulo.imagen != null) {
                PreparedStatement stmt_3 = conexion.prepareStatement(
                        "insert into imagenes_articulo values (0,?,(select id_articulo from articulos where descripcion=?))");
                try {
                    stmt_3.setBytes(1, articulo.imagen);
                    stmt_3.setString(2, articulo.descripcion);
                    stmt_3.executeUpdate();
                } finally {
                    stmt_3.close();
                }
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
        return Response.ok().build();
    }

    // @POST
    // @Path("consulta_articulos")
    // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response consulta(@FormParam("email") String email) throws Exception {
    // Connection conexion = pool.getConnection();

    // try {
    // PreparedStatement stmt_1 = conexion.prepareStatement(
    // "SELECT
    // a.email,a.nombre,a.apellido_paterno,a.apellido_materno,a.fecha_nacimiento,a.telefono,a.genero,b.foto
    // FROM usuarios a LEFT OUTER JOIN fotos_usuarios b ON a.id_usuario=b.id_usuario
    // WHERE email=?");
    // try {
    // stmt_1.setString(1, email);

    // ResultSet rs = stmt_1.executeQuery();
    // try {
    // if (rs.next()) {
    // Usuario r = new Usuario();
    // r.email = rs.getString(1);
    // r.nombre = rs.getString(2);
    // r.apellido_paterno = rs.getString(3);
    // r.apellido_materno = rs.getString(4);
    // r.fecha_nacimiento = rs.getString(5);
    // r.telefono = rs.getString(6);
    // r.genero = rs.getString(7);
    // r.foto = rs.getBytes(8);
    // return Response.ok().entity(j.toJson(r)).build();
    // }
    // return Response.status(400).entity(j.toJson(new Error("El email no
    // existe"))).build();
    // } finally {
    // rs.close();
    // }
    // } finally {
    // stmt_1.close();
    // }
    // } catch (Exception e) {
    // return Response.status(400).entity(j.toJson(new
    // Error(e.getMessage()))).build();
    // } finally {
    // conexion.close();
    // }
    // }

}