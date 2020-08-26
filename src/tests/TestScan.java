package tests;

import java.util.Scanner;

public class TestScan {
	public static void main(String[] args) {
		
		Scanner scanMot = null; //Pour la saisie du mot du client
		Scanner scanTraject = null; //Pour la saisie de la trajectoire du client
		String mot;
		String trajectoire;
			
		for(int i = 0; i<3; i++) {
				
			System.out.print("Mot : ");
			scanMot = new Scanner(System.in); //Saisie du mot du client sur l'entree standard
			mot = scanMot.nextLine(); //On recupere le mot saisi par le client
					
			System.out.print("Trajectoire : ");
			scanTraject = new Scanner(System.in); //Saisie de la trajectoire du client sur l'entree standard
			trajectoire = scanTraject.nextLine(); //On recupere la trajectoire saisie par le client
					
			System.out.println("\nVous avez saisi le mot : " + mot + "\net la trajectoire : " + trajectoire);
		}
				
		if(scanMot != null) scanMot.close();
		if(scanTraject != null) scanTraject.close();
		
	}

}
