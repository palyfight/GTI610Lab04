import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Cette classe permet la reception d'un paquet UDP sur le port de reception
 * UDP/DNS. Elle analyse le paquet et extrait le hostname
 * 
 * Il s'agit d'un Thread qui ecoute en permanance pour ne pas affecter le
 * deroulement du programme
 * 
 * @author Max
 *
 */

public class UDPReceiver extends Thread {
	/**
	 * Les champs d'un Packet UDP -------------------------- En-tete (12
	 * octects) Question : l'adresse demande Reponse : l'adresse IP Autorite :
	 * info sur le serveur d'autorite Additionnel : information supplementaire
	 */

	/**
	 * Definition de l'En-tete d'un Packet UDP
	 * --------------------------------------- Identifiant Parametres QDcount
	 * Ancount NScount ARcount
	 * 
	 * L'identifiant est un entier permettant d'identifier la requete.
	 * parametres contient les champs suivant : QR (1 bit) : indique si le
	 * message est une question (0) ou une reponse (1). OPCODE (4 bits) : type
	 * de la requete (0000 pour une requete simple). AA (1 bit) : le serveur qui
	 * a fourni la reponse a-t-il autorite sur le domaine? TC (1 bit) : indique
	 * si le message est tronque. RD (1 bit) : demande d'une requete recursive.
	 * RA (1 bit) : indique que le serveur peut faire une demande recursive.
	 * UNUSED, AD, CD (1 bit chacun) : non utilises. RCODE (4 bits) : code de
	 * retour. 0 : OK, 1 : erreur sur le format de la requete, 2: probleme du
	 * serveur, 3 : nom de domaine non trouve (valide seulement si AA), 4 :
	 * requete non supportee, 5 : le serveur refuse de repondre (raisons de
	 * s�ecurite ou autres). QDCount : nombre de questions. ANCount, NSCount,
	 * ARCount : nombre d�entrees dans les champs �Reponse�, Autorite,
	 * Additionnel.
	 */

	protected final static int BUF_SIZE = 1024;
	protected String SERVER_DNS = null;// serveur de redirection (ip)
	protected int portRedirect = 53; // port de redirection (par defaut)
	protected int port; // port de r�ception
	private String adrIP = null; // bind ip d'ecoute
	private String DomainName = "none";
	private String DNSFile = null;
	private boolean RedirectionSeulement = false;

	private class ClientInfo { // quick container
		public String client_ip = null;
		public int client_port = 0;
	};

	private HashMap<Integer, ClientInfo> Clients = new HashMap<>();

	private boolean stop = false;

	public UDPReceiver() {
	}

	public UDPReceiver(String SERVER_DNS, int Port) {
		this.SERVER_DNS = SERVER_DNS;
		this.port = Port;
	}

	public void setport(int p) {
		this.port = p;
	}

	public void setRedirectionSeulement(boolean b) {
		this.RedirectionSeulement = b;
	}

	public String gethostNameFromPacket() {
		return DomainName;
	}

	public String getAdrIP() {
		return adrIP;
	}

	private void setAdrIP(String ip) {
		adrIP = ip;
	}

	public String getSERVER_DNS() {
		return SERVER_DNS;
	}

	public void setSERVER_DNS(String server_dns) {
		this.SERVER_DNS = server_dns;
	}

	public void setDNSFile(String filename) {
		DNSFile = filename;
	}

	public void run() {
		try {
			DatagramSocket serveur = new DatagramSocket(this.port); // *Creation
																	// d'un
																	// socket
																	// UDP

			// *Boucle infinie de recpetion
			while (!this.stop) {
				byte[] buff = new byte[0xFF];
				DatagramPacket paquetRecu = new DatagramPacket(buff,
						buff.length);
				System.out
						.println("Serveur DNS  " + serveur.getLocalAddress()
								+ "  en attente sur le port: "
								+ serveur.getLocalPort());

				// *Reception d'un paquet UDP via le socket
				serveur.receive(paquetRecu);

				System.out.println("paquet recu du  " + paquetRecu.getAddress()
						+ "  du port: " + paquetRecu.getPort());

				// *Creation d'un DataInputStream ou ByteArrayInputStream pour
				// manipuler les bytes du paquet

				ByteArrayInputStream TabInputStream = new ByteArrayInputStream(
						paquetRecu.getData());


				TabInputStream.skip(2); //Skip le ID
				byte qr = (byte) TabInputStream.read(); // Lire QR OPCODE AA TC
														// RD

				TabInputStream.skip(3); // Skip RA QDCOUNT

				// ANCOUNT
				int ancount = (TabInputStream.read() << 8 + TabInputStream
						.read() & 0xFF);
				TabInputStream.skip(4); // Skip NSCOUNT ARCOUNT


				// Lire le nom de domaine
				String domaine = "";
				for (int i = TabInputStream.read(); i != 0;) {

					for (; i > 0; i--)
						domaine += String.valueOf(Character
								.toChars(TabInputStream.read()));
					
					i = TabInputStream.read();
					if (i > 0)
						domaine += ".";
				}
				/*if(domaine.contains(".ens.ad.etsmtl.ca"))
					domaine = domaine.replace(".ens.ad.etsmtl.ca", "");*/ // On enleve le bout ajouter par l'ecole

				// Skip QTYPE QCLASS NAME TYPE CLASS TTL RDLENGTH
				TabInputStream.skip(16);

				String ipAdr = "";
				short digit = 0;
				for (int i = 0; i < 4; i++) {
					digit = (short) TabInputStream.read();
					ipAdr += String.valueOf(digit);
					if (i < 3)
						ipAdr += ".";
				}

				// ****** Dans le cas d'un paquet requete *****
				if ((qr & (1 << 7)) == 0) {

					// *Si le mode est redirection seulement
					if (this.RedirectionSeulement) {

						serveur.send(new DatagramPacket(paquetRecu.getData(),
								paquetRecu.getLength(), InetAddress
										.getByName(this.getSERVER_DNS()),
								this.portRedirect));
					} else {

						// *Rechercher l'adresse IP associe au Query Domain name
						// dans le fichier de correspondance de ce serveur
						QueryFinder qFinder = new QueryFinder(DNSFile);
						List<String> adresse = qFinder.StartResearch(domaine);

						if (adresse.isEmpty())
							serveur.send(new DatagramPacket(
									paquetRecu.getData(),
									paquetRecu.getLength(),
									InetAddress.getByName(this.getSERVER_DNS()),
									this.portRedirect));

						else {

							// *Creer le paquet de reponse a l'aide du
							// UDPAnswerPaquetCreator
							byte[] data = UDPAnswerPacketCreator.getInstance()
									.CreateAnswerPacket(paquetRecu.getData(),
											adresse);

							// *Placer ce paquet dans le socket
							// *Envoyer le paquet
							serveur.send(new DatagramPacket(data, data.length,
									paquetRecu.getAddress(), paquetRecu
											.getPort()));
						}
					}

				} else {

					// *Ajouter la ou les correspondance(s) dans le fichier DNS
					// si elles ne y sont pas deja
					QueryFinder qFinder = new QueryFinder(DNSFile);
					List<String> adresse = qFinder.StartResearch(domaine);

					// *Ajouter la ou les correspondance(s) dans le fichier DNS
					// si elles ne y sont pas deja
					if (adresse.isEmpty()) {
						AnswerRecorder recorder = new AnswerRecorder(DNSFile);
						recorder.StartRecord(domaine, ipAdr);
					}

					// *Faire parvenir le paquet reponse au demandeur original,
					// ayant emis une requete avec cet identifiant
					// *Placer ce paquet dans le socket
					// *Envoyer le paquet
					// *Creer le paquet de reponse a l'aide du
					// UDPAnswerPaquetCreator

					adresse = qFinder.StartResearch(domaine);
					byte[] data = UDPAnswerPacketCreator.getInstance()
							.CreateAnswerPacket(paquetRecu.getData(), adresse);

					// *Placer ce paquet dans le socket
					// *Envoyer le paquet
					serveur.send(new DatagramPacket(data, data.length,
							paquetRecu.getAddress(), paquetRecu.getPort()));
				}

				System.out.println();
			}
			// serveur.close(); //closing server
		} catch (Exception e) {
			System.err.println("Probl鮥 de l'exꤵtion :");
			e.printStackTrace(System.err);
		}
	}
}
