import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Examen {
	public static void main(String[] args) {
		try {
			Socket conn = new Socket("sisdis.sytes.net", 50001);

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			DataInputStream in = new DataInputStream(conn.getInputStream());

			// enviar datos
			// out.writeInt(123);
			// out.writeDouble(123123123.687974);
			// out.write("hola".getBytes());
			// out.writeUTF("hola");

			// recibir datos
			// byte[] buffer = new byte[64];
			// read(in, buffer, 0, 64);
			long t2 = in.readLong();
			System.out.println(t2);
			long t3 = in.readLong();
			System.out.println(t3);
			long t1 = 1632744960;
			long t4 = t3 + 3;
			long res = ((t4 - t1) - (t3 - t2)) / 2;
			System.out.println(res + t3);
			// System.out.println(new String(buffer, "UTF-8"));
			// System.out.println(1632744980 -);
			// System.out.println(1632744980 -);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
		while (longitud > 0) {
			int n = f.read(b, posicion, longitud);
			posicion += n;
			longitud -= n;
		}
	}
}

/*
 * t1 = 1632744960 t2= 1632744980 t3= 1632744995 t4= 1632744998
 */
