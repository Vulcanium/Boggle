package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	protected static final int PORT=2018;
	protected Socket serveur = null;
	protected BufferedReader inchannel;
	protected DataOutputStream outchannel;
	protected boolean deco = false; //Quand le joueur se deconnecte
	protected String pseudo;
	protected String mot;
	protected String trajectoire;
	
	public Client(String nameHost, String pseudo) {
		this.pseudo = pseudo;
		try {
			serveur = new Socket(nameHost, PORT);
			inchannel = new BufferedReader(new InputStreamReader (serveur.getInputStream()));
			outchannel = new DataOutputStream(serveur.getOutputStream());
			outchannel.writeChars("CONNEXION/" + pseudo + "/" + "\n"); //Pour initier la connexion avec le serveur
		}
		
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
	}
	
	
	public void traitementClient() {
		
		try {
			
			while(!deco) {
				
				String commande = inchannel.readLine();
				String [] sousCommande = commande.split("/"); //Chaque commande est separee par un slash
		
				if(sousCommande.length > 3) { //sousCommande.length == 0 est traite par le default du switch ci-apres
					System.err.println("Erreur du serveur : Nombre de commandes incorrect !");
					System.exit(1);
				}
				
				switch(sousCommande[0]) {
				
				/* Connexion */
				case "BIENVENUE" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Tirage non renseigné !");
						System.exit(1);
					} else if(sousCommande[2].equals("\n")){
						System.err.println("Erreur du serveur : Scores non renseignés !");
						System.exit(1);
					} else {
						System.out.println("Bienvenue " + pseudo + " !" + "\nTirage : " + sousCommande[1] + "\nScores : " + sousCommande[2]);
					}
					break;
					
				case "CONNECTE" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Pseudo non renseigné !");
						System.exit(1);
					} else {
						System.out.println(sousCommande[1] + " s'est connecté");
					}
					break;
				
				/* Deconnexion */
				case "DECONNEXION" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Pseudo non renseigné !");
						System.exit(1);
					} else {
						outchannel.writeChars("SORT/" + pseudo + "/" + "\n");
						System.out.println(pseudo + " s'est déconnecté");
						deco = true; //Pour sortir de la boucle
					}
					break;
					
				/* Debut session */
				case "SESSION" :
					System.out.println("Début de la partie !");
					break;
				
				/* Fin session */	
				case "VAINQUEUR" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Scores non renseignés !");
						System.exit(1);
					} else {
						System.out.println("Partie terminée, scores finaux : " + sousCommande[1]);
					}
					break;
					
				/* Phase de recherche */	
				case "TOUR" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Tirage non renseigné !");
						System.exit(1);
					} else {
						System.out.println("Début du tour, tirage : " + sousCommande[1]);
						formuleMots();
					}
					break;
				
				case "MVALIDE" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Mot du client non renseigné !");
						System.exit(1);
					} else {
						System.out.println("Le mot " + "\"" + sousCommande[1] + "\"" + " est valide");
					}
					break;
					
				case "MINVALIDE" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Mot du client non renseigné !");
						System.exit(1);
					} else {
						System.out.println("Le mot " + "\"" + sousCommande[1] + "\"" + " est invalide");
					}
					break;
				
				/* Phase de resultat */	
				case "BILANMOTS" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Mots proposés par le client non renseignés !");
						System.exit(1);
					} else if(sousCommande[2].equals("\n")){
						System.err.println("Erreur du serveur : Scores non renseignés !");
						System.exit(1);
					} else {
						System.out.println("Fin du tour !\n" + "Mots proposés et validés : " + sousCommande[1] + "\nScores : " + sousCommande[2]);
					}
					break;
					
					
				/* Chat */
				case "RECEPTION" :
					if(sousCommande[1].equals("\n")) {
						System.err.println("Erreur du serveur : Message non renseigné !");
					} else {
						System.out.println("Le message a bien été reçu");
					}
					break;
					
					
				/* Commande non reconnue */	
				default :
					System.err.println("Erreur du serveur : Commande non reconnue !");
					break;
					
				}			
			}
		}
		
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		finally {
			try {
				if(serveur != null) serveur.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	private void formuleMots() {
		
		Scanner scanMot = null; //Pour la saisie du mot du client
		Scanner scanTraject = null; //Pour la saisie de la trajectoire du client
		
		try {
		
			while(true) {
				
				String commande = inchannel.readLine();
				String [] sousCommande = commande.split("/"); //Chaque commande est separee par un slash
				
				if(sousCommande[0].equals("RFIN")) { 
					System.out.println("Fin du temps imparti !");
					break; 
				}
				
				System.out.print("Mot : ");
				scanMot = new Scanner(System.in); //Saisie du mot du client sur l'entree standard
				mot = scanMot.nextLine(); //On recupere le mot saisi par le client
				
				System.out.print("Trajectoire : ");
				scanTraject = new Scanner(System.in); //Saisie de la trajectoire du client sur l'entree standard
				trajectoire = scanTraject.nextLine(); //On recupere la trajectoire saisie par le client
				
				outchannel.writeChars("TROUVE/" + mot + "/" + trajectoire + "/" + "\n");
				
			}
			
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			if(scanMot != null) scanMot.close();
			if(scanTraject != null) scanTraject.close();
		}
		
	}
	
}
