package tests;

import client.Client;
import serveur.Serveur;

public class BoggleMain {

	public static void main(String[] args) {
		
		String path = args[0]; //Chemin du fichier contenant le dictionnaire
		int tempsLimite = Integer.parseInt(args[1]); //Limite de temps d'un tour de jeu (valeur comprise entre 180sec et 300sec)
		String host = args[2]; //Nom de l'hôte
		String pseudo = args[3]; //Pseudo du joueur
		
		Serveur serv = new Serveur(tempsLimite, path);
		Client cli = new Client(host, pseudo);
		cli.traitementClient();
		
	}

}
