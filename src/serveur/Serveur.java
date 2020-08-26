package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur extends Thread {
	
	protected static final int PORT=2018;
	protected ServerSocket ecoute;
	protected int limiteChrono;
	protected String dictionnaire;
	
	public Serveur(int limiteChrono, String dictionnaire) {
		
		if(limiteChrono<180 || limiteChrono>300) { //3min = 180sec et 5min = 300sec
			System.err.println("Erreur : Le compte-à-rebours doit avoir une valeur comprise entre 180 et 300");
			System.exit(1);
		}
		
		this.limiteChrono = limiteChrono;
		this.dictionnaire = dictionnaire;
		
		try {
			ecoute = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		this.start();
		
	}
	
	public void run() {
		
		while(true) {
			try {
				Socket client = ecoute.accept();
				Traitement traite = new Traitement(client, limiteChrono, dictionnaire);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		
	}
	
}
