package tests;

public class TestGrille {

	public static void main(String[] args) {
		
		String [] des = new String[17];
		String maGrille = "";
		
		des[0] = ""; //Pour avoir un saut de ligne toutes les 4 lettres
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
			if(x%4 == 0) maGrille += "\n";
		}
		
		System.out.println(maGrille);
	}

}
