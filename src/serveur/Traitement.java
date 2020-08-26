package serveur;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class Traitement extends Thread {
	
	protected Socket cli;
	protected BufferedReader inchannel;
	protected DataOutputStream outchannel;
	protected int limiteChrono; //Compte-a-rebours (entre 180 et 300sec)
	protected BufferedReader lecteurDico;
	protected String tirage; //Lettres actuellement tirees (equivalent a grille de l'enonce)
	protected int tour = 0; //Tour actuel
	protected final int LIMITETOUR = 10; //Limite de tour avant la fin d'une session
	protected String scores; //Sous la forme tour*user1*score1*...
	protected int points; //Nombre de points amasses par le joueur
	protected boolean deco; //Pour savoir si le joueur est deconnecte
	protected String motsproposes; //Ensemble des mots proposes et valides des clients
	
	public Traitement(Socket client, int limiteChrono, String dictionnaire) {
		
		this.limiteChrono = limiteChrono;
		
		try {
			lecteurDico = new BufferedReader(new FileReader(dictionnaire));
			inchannel = new BufferedReader(new InputStreamReader (client.getInputStream()));
			outchannel = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		cli = client;
		deco = false;
		this.start();
	}
	
	
	public void run() {
		try {
			
			Chrono currentchrono = new Chrono();
			
			while(!deco) {
				
				String commande = inchannel.readLine();
				String [] sousCommande = commande.split("/"); //Chaque commande est separee par un slash
				
				if(sousCommande.length > 3) { //sousCommande.length == 0 est traite par le default du switch ci-apres
					System.err.println("Erreur du client : Nombre de commandes incorrect !");
					System.exit(1);
				}
				
				if(tour == 0) { //Debut de session
					points = 0;
					scores = tour + "*" + points;
					outchannel.writeChars("SESSION/" + "\n");
					commande = "TOUR/" + "\n";
					sousCommande = commande.split("/");
				}
				
				if(currentchrono.getDureeSec() == limiteChrono) { //Fin du temps imparti, donc fin du tour
					currentchrono.stop();
					outchannel.writeChars("RFIN/" + "\n");
					outchannel.writeChars("BILANMOTS/" + motsproposes + scores + "/" + "\n");
					sleep(10000); //Delai avant le debut d'un nouveau tour (10s = 10000ms)
					
					if(tour != LIMITETOUR) {
						commande = "TOUR/" + "\n";
						sousCommande = commande.split("/");
					} 
					else { //Fin de session
						outchannel.writeChars("VAINQUEUR/" + scores + "/" + "\n");
						tour = 0;
						sleep(10000); //Delai avant le debut d'une nouvelle session (10s = 10000ms)
					}
				}
				
				switch(sousCommande[0]) {
					
				/* Connexion */
				case "CONNEXION" :
					if(!sousCommande[1].equals("\n")) {
						System.out.println("Connexion de \'" + sousCommande[1] + "\'");
						outchannel.writeChars("BIENVENUE/" + tirage + "/" + scores + "/" + "\n");
						outchannel.writeChars("CONNECTE/" + sousCommande[1] + "/" + "\n");
					} else {
						System.err.println("Erreur du client : Pseudo non renseigné !");
						System.exit(1);
					}
					break;
					
				/* Deconnexion */
				case "SORT" :
					if(!sousCommande[1].equals("\n")) {
						System.out.println("Deconnexion de \'" + sousCommande[1] + "\'");
						outchannel.writeChars("DECONNEXION/" + sousCommande[1] + "/" + "\n");
						deco = true; //Pour sortir de la boucle
					} else {
						System.err.println("Erreur du client : Pseudo non renseigné !");
						System.exit(1);
					}
					break;
					
				/* Phase de recherche */
				case "TOUR" :
					tour++;
					tirage = genereGrille();
					currentchrono.start();
					outchannel.writeChars("TOUR/" + tirage + "/" + "\n");
					break;
				
				case "TROUVE" :
					System.out.println("Mot : " + sousCommande[1] + " avec la trajectoire : " + sousCommande[2]);
					if(motCorrect(sousCommande[1], sousCommande[2])) {
						motsproposes += sousCommande[1];
						points += bareme(sousCommande[1]);
						scores = tour + "*" + points;
						outchannel.writeChars("MVALIDE/" + sousCommande[1] + "/" + "\n");
					} else {
						outchannel.writeChars("MINVALIDE/" + sousCommande[1] + "/" + "\n");
					}
					break;
					
					
				/* Chat */
				case "ENVOI" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du client : Message non renseigné !");
					} else {
						System.out.println(sousCommande[1]);
						outchannel.writeChars("RECEPTION/" + sousCommande[1] + "/" + "\n");
					}
					break;

				
				/* Commande non reconnue */	
				default :
					System.err.println("Erreur du client : Commande non reconnue !");
					break;
					
				}
			}
		}
		
		catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		finally {
			try {
				cli.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	private boolean motCorrect(String mot, String trajectoire) {
		try {
			
			if(mot.length() < 3) return false;
			
			String line;
			String [] wordsinline;
			BufferedReader tmplecteur = lecteurDico;
			
			while((line = tmplecteur.readLine()) != null) {
				wordsinline = line.split("|"); //Dans le GLAFF, les mots d'une ligne sont separes par des pipes
				for(int i = 0; i<line.length(); i++) {
					if(wordsinline[i].equals(mot)) {
						return true;
					}
				}
			}
			
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	private int bareme(String mot) {
		
		if(mot.length() <= 4) return 1; //mot.length() est au minimum egal a 3 (longueur verifiee par la methode motCorrect)
		if(mot.length() == 5) return 2;
		if(mot.length() == 6) return 3;
		if(mot.length() == 7) return 5;
		return 11;
	}
	
	
	private String genereGrille() {
		
		String [] des = new String[17];
		String maGrille = "";
		
		des[0] = ""; //Pour avoir 4 lettres par lignes (voir le if ci-dessous)
		des[1] = "ETUKNO";
		des[2] = "EVGTIN";
		des[3] = "DECAMP";
		des[4] = "IELRUW";
		des[5] = "EHIFSE";
		des[6] = "RECALS";
		des[7] = "ENTDOS";
		des[8] = "OFXRIA";
		des[9] = "NAVEDZ";
		des[10] = "EIOATA";
		des[11] = "GLENYU";
		des[12] = "BMAQJO";
		des[13] = "TLIBRA";
		des[14] = "SPULTE";
		des[15] = "AIMSOR";
		des[16] = "ENHRIS";

		
		for(int x = 1; x < des.length; x++){
			int i = (int)(Math.random() * des[x].length());
			maGrille += des[x].charAt(i);
			if(x%4 == 0) maGrille += "\n"; //Pour avoir un saut de ligne toutes les 4 lettres
		}
		
		return maGrille;
	}
	
}
