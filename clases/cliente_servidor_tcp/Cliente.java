import java.net.Socket;

public class Cliente{
	public static void main(String[] args) {
		try{
			Socket conn = new Socket("localhost", 50000);

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			DataInputStream in = new DataInputStream(conn.getInputStream());

			out.close();
			entrada.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}