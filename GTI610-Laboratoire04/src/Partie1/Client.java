package Partie1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) {
		Socket sock = new Socket();
		String text = "";
		Scanner keyboard = new Scanner(System.in);
		BufferedWriter bw;
		//PrintWriter pw;
		OutputStream os = null;
		
		try {
			sock = new Socket("localhost", 8000);
			os = sock.getOutputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(sock.isConnected()){
			while(true){
				/*System.out.println("Entrez du texte: ");
				keyboard = new Scanner(System.in);
				text = keyboard.nextLine();
				System.out.println("\n");*/
				bw = new BufferedWriter(new OutputStreamWriter(os));
				try {
					//os.write("HELLO WORLD!!!".getBytes());
					bw.write("Hello World!!");
					/*os.write(28);
					os.flush();*/
					bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} //while
		} //if
		
	}

}
