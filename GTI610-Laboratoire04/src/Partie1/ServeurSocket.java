package Partie1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class ServeurSocket {

	public static void main(String[] args) {
		ServerSocket serveur = null;
		byte[] buff = new byte[256];
		
		
		try {
			serveur = new ServerSocket(8000);
		
		} catch(IOException e) {
			
			System.out.print("\n"+e);
			System.exit(1);
		}

		boolean estActif = true;
	    Socket socket = null;
	    BufferedReader in = null;
	    String ligne = null;
	    InputStream ins = null;

		while(estActif){
			
		    try {
		    	socket = serveur.accept();		    	
		    	System.out.println("Connected!");
		    	ins = socket.getInputStream();
		    	ins.read(buff);
		    	System.out.println(buff[0]);
				/*in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				while ((ligne = in.readLine()) != null)
			    {
					if(ligne.equals("STOP!!")) estActif = false;
			        System.out.print(ligne + "\n");
			    }*/

				
			} catch (IOException e) {

				System.out.print("\n"+e);
				System.exit(1);
			}
		}

		try {
			socket.close();
			serveur.close();
			
		} catch (IOException e) {
			
			System.out.print("\n"+e);
			System.exit(1);
		}
	}
}
