import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Cliente {
	public static void main(String[] args) {
		try {
			Socket conn = new Socket("localhost", 50000);

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			DataInputStream in = new DataInputStream(conn.getInputStream());

			// enviar datos
			out.writeInt(123);
			out.writeDouble(123123123.687974);
			out.write("hola".getBytes());
			// out.writeUTF("hola");

			// recibir datos
			byte[] buffer = new byte[4];
			read(in, buffer, 0, 4);
			System.out.println(new String(buffer, "UTF-8"));
			// System.out.println(in.readUTF());

			// enviar conjuntos de doubles eficientemente
			ByteBuffer b = ByteBuffer.allocate(5 * 8);
			b.putDouble(1.1);
			b.putDouble(1.2);
			b.putDouble(1.3);
			b.putDouble(1.4);
			b.putDouble(1.5);
			byte[] a = b.array();
			out.write(a);

			int numeros = 10000;
			long inicio = System.currentTimeMillis();
			// enviar 10k doubles
			for (int i = 1; i <= numeros; i++) {
				out.writeDouble((double) i);
			}

			long total = System.currentTimeMillis() - inicio;
			System.out.println("tiempo en enviar " + numeros + " flotantes:" + total + " ms");

			b = ByteBuffer.allocate(8 * numeros);
			for (int i = 1; i <= numeros; i++) {
				b.putDouble((double) i);
			}
			a = b.array();
			inicio = System.currentTimeMillis();
			out.write(a);
			total = System.currentTimeMillis() - inicio;
			System.out.println("tiempo en enviar " + numeros + " flotantes:" + total + " ms");

			out.close();
			in.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
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