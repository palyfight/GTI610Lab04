package Partie1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		Socket socket = new Socket();
		Scanner keyboard = new Scanner(System.in);

		try {
			socket = new Socket("localhost", 2015);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (socket.isConnected()) {

			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				String ligne = "Hello World!";

				System.out.println("Quoi? :");
				ligne = keyboard.nextLine();
				
				while (!(ligne.trim().equals("ciao")))
					out.println(ligne);

			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				try {
					
					socket.shutdownOutput();
					socket.close();
				} catch (IOException e) {
					
					e.printStackTrace();
					System.exit(1);
				}
			}

		} // if
	}
}
