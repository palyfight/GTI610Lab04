package Partie1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		Socket socket = new Socket();
		Scanner keyboard = new Scanner(System.in);

		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer buffer = new StringBuffer();
		String message = "";

		try {
			socket = new Socket("localhost", 2015);
			
			if (socket.isConnected()) {


				PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String line = "";
				do{
					System.out.println("Quoi? :");
					line = keyboard.nextLine(); 
					if(line != "\n"){
						out.println(line);
						out.flush();
						System.out.println(br.readLine());
					}
				}while(!(line.trim().equalsIgnoreCase("exit")));

				out.close();
				br.close();
				socket.close();

			}
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} 


		// if
	}
} 

/*public class Client {

	public static void main(String[] args) {
		while(true){
			System.out.print("LELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELELE");
		}
	}
}*/