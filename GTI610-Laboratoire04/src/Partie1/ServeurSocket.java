package Partie1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurSocket {

	public static void main(String[] args) {
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(2015);

			while(true){
				new MyThread(server.accept()).start();
			}

		} catch (IOException e) {

			System.out.print("\n" + e);
			System.exit(1);
		}
		finally {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static class MyThread extends Thread{
		BufferedReader br;
		PrintWriter out;
		String ipAddress;
		int portNum;
		Socket socket;


		MyThread(Socket socket){			
			try {
				this.socket = socket;
				System.out.println("Connection ouverte");
				this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.out = new PrintWriter(socket.getOutputStream(), true);
				this.ipAddress = socket.getInetAddress().toString();
				this.portNum = socket.getPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void run(){
			String ligne = "";

			try {
				while ((ligne = br.readLine()) != null || socket.isConnected()) {
					System.out.println("Message From Client: "+ ligne + " " + ipAddress + ":" + portNum );
					out.println(ligne.toUpperCase());
					out.flush();
				}
				System.out.println("Connection close");
				br.close();
				out.close();
				socket.close();
				this.interrupt();
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}

/*public class ServeurSocket {

	public static void main(String[] args) {
		while(true){
			System.out.print("LELELELELELELELELLELELELELELELELELELELELELELELELELLELE");
		}
	}
}*/