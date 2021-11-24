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

            conexion.setAutoCommit(false);
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
            conexion.commit();
        } catch (Exception e) {
            conexion.rollback();
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.setAutoCommit(true);
            conexion.close();
        }
        return Response.ok().build();
    }

    @POST
    @Path("consulta_articulos")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consulta(@FormParam("busqueda") String busqueda) throws Exception {
        Connection conexion = pool.getConnection();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement(
                    "SELECT a.descripcion, a.precio, b.imagen FROM articulos a LEFT OUTER JOIN imagenes_articulo b ON a.id_articulo=b.id_articulo WHERE descripcion LIKE ?");
            try {
                stmt_1.setString(1, '%' + busqueda + '%');
                ResultSet rs = stmt_1.executeQuery();
                try {
                    ArrayList<Articulo> articulos = new ArrayList<Articulo>();
                    while (rs.next()) {
                        Articulo art = new Articulo();
                        art.descripcion = rs.getString(1);
                        art.precio = rs.getFloat(2);
                        art.imagen = rs.getBytes(3);
                        articulos.add(art);
                    }

                    if (articulos.isEmpty()) {
                        return Response.status(201).entity(j.toJson("No se encontraron articulos con esa descripcion"))
                                .build();
                    } else {
                        return Response.ok().entity(j.toJson(articulos)).build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }

    @POST
    @Path("alta_carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response altaCarrito(@FormParam("descripcion") String descripcion, @FormParam("cantidad") int cantidad)
            throws Exception {
        Connection conexion = pool.getConnection();
        int cantidad_actual = -1;
        int id_articulo = -1;
        try {
            PreparedStatement stmt_1 = conexion
                    .prepareStatement("select id_articulo, cantidad from articulos where descripcion=?");
            try {
                stmt_1.setString(1, descripcion);
                ResultSet rs = stmt_1.executeQuery();
                try {
                    if (rs.next()) {
                        id_articulo = rs.getInt(1);
                        cantidad_actual = rs.getInt(2);
                    } else {
                        return Response.status(400)
                                .entity(j.toJson(new Error("No hay un articulo con esa descripcion"))).build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }

            if (cantidad <= cantidad_actual) {
                conexion.setAutoCommit(false);
                // update de cantidad Articulos
                PreparedStatement stmt_2 = conexion
                        .prepareStatement("UPDATE articulos SET cantidad=? WHERE id_articulo=?");
                try {
                    stmt_2.setInt(1, cantidad_actual - cantidad);
                    stmt_2.setInt(2, id_articulo);
                    stmt_2.executeUpdate();
                } finally {
                    stmt_2.close();
                }

                // agregar al carrito
                PreparedStatement stmt_3 = conexion.prepareStatement("insert into carrito_compra VALUES (0,?,?)");
                try {
                    stmt_3.setInt(1, id_articulo);
                    stmt_3.setInt(2, cantidad);
                    stmt_3.executeUpdate();
                } finally {
                    stmt_3.close();
                }
                conexion.commit();
            } else {
                return Response.status(201).entity(j.toJson(cantidad_actual)).build();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.setAutoCommit(true);
            conexion.close();
        }
        return Response.ok().build();
    }

    @POST
    @Path("consulta_carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultaCarrito() throws Exception {
        Connection conexion = pool.getConnection();

        try {

            PreparedStatement stmt_1 = conexion.prepareStatement(
                    "select a.descripcion, a.precio, b.imagen, c.cantidad from articulos a left outer join imagenes_articulo b on a.id_articulo=b.id_articulo left outer join carrito_compra c on a.id_articulo=c.id_articulo where c.cantidad is not null");
            try {
                ResultSet rs = stmt_1.executeQuery();
                try {
                    ArrayList<Articulo> articulos = new ArrayList<Articulo>();
                    while (rs.next()) {
                        Articulo art = new Articulo();
                        art.descripcion = rs.getString(1);
                        art.precio = rs.getFloat(2);
                        art.imagen = rs.getBytes(3);
                        art.cantidad = rs.getInt(4);
                        articulos.add(art);
                    }

                    if (articulos.isEmpty()) {
                        return Response.status(202).entity(j.toJson("No se encontraron articulos en el carrito"))
                                .build();
                    } else {
                        return Response.ok().entity(j.toJson(articulos)).build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }

}