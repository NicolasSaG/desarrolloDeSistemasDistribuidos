/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 *
 * @author fnico
 */
public class AdaptadorGsonBase64 implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
    }

    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        // jax-rs reemplaza cada "+" por " ", pero el decodificador Base64 no reconoce "
        // "
        String s = json.getAsString().replaceAll("\\ ", "+");
        return Base64.getDecoder().decode(s);
    }
}