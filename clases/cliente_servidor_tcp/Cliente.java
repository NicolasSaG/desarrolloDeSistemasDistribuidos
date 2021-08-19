import java.net.Socket;

public class Cliente{
	public static void main(String[] args) {
		try{
			Socket conn = new Socket("localhost", 50000);

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			DataInputStream in = new DataInputStream(conn.getInputStream());

			//enviar datos
			out.writeInt(123);
			out.writeDouble(123123123.687974);
			out.write("hola".getBytes());

			//recibir datos
			byte [] buffer = new byte[4];
			in.read(buffer, 0, 4);
			System.out.println(new String(buffer, "UTF-8"));
			

			out.close();
			entrada.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}