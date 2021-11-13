import com.google.gson.*;

public class Articulo {
    String descripcion;
    int cantidad;
    float precio;
    byte[] imagen;

    public Articulo(String descripcion, int cantidad, float precio, byte[] imagen) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.imagen = imagen;
    }

    // @FormParam necesita un metodo que convierta una String al objeto de tipo
    // Usuario
    public static Articulo valueOf(String s) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64()).create();
        return (Articulo) j.fromJson(s, Articulo.class);
    }
}