package Partie1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurSocket {

	public static void main(String[] args) {
		ServerSocket serveur = null;

		try {
			serveur = new ServerSocket(2015);

		} catch (IOException e) {

			System.out.print("\n" + e);
			System.exit(1);
		}

		Socket socket = null;
		BufferedReader in = null;
		//String ligne = null;

		while (true) {

			try {
				socket = serveur.accept();
				System.out.println("Connection ouverte");
				
				String ip = socket.getInetAddress().toString();
				int port = socket.getPort();

				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				/*while ((ligne = in.readLine()) != null) {
					
					System.out.println("Reçu: " + ligne);
				}*/
				
				MyThread thread = new MyThread(in, ip, port);
				thread.start();
					
				System.out.println("Connection fermée");

			} catch (IOException e) {
				if (!socket.isClosed()) {
					e.printStackTrace();
					System.exit(1);
				}
			} /*finally {
				try {					
					socket.shutdownInput();
					socket.close();
				} catch (IOException e) {

					e.printStackTrace();
					System.exit(1);
				}
			}*/

		}
	}
	
	public static class MyThread extends Thread{
		BufferedReader br;
		String ipAddress;
		int portNum;
		
		MyThread(BufferedReader b, String ip, int port){
			this.br = b;
			this.ipAddress = ip;
			this.portNum = port;
		}
		
		public void run(){
			/*String ligne = "";

			try {
				while ((ligne = br.readLine()) != null) {
					System.out.println("From: " + sock.getInetAddress() +":" + sock.getPort() + "\nMessage: " + ligne);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			System.out.println("THIS IS A TEST NIGGUH! " + ipAddress + ":" + portNum);
		}
	}
}
