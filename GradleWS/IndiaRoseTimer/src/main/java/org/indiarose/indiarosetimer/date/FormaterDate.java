package org.indiarose.indiarosetimer.date;


public class FormaterDate {

	public static final int HEURE = 0;
	public static final int MINUTE = 1;
	public static final int SECONDE = 2;

	public static int [] formaterDate(float timeSeconde){
		int heure = (int) (timeSeconde/3600);
		float remaining = timeSeconde-(heure*3600);
		int minute = (int) (remaining/60);
		int seconde = (int) (timeSeconde-(heure*3600)-(minute*60));
		int[] tab = {heure,minute,seconde};
		return tab;
	}

	public static String timeToString(int heure, int minute , int seconde){

		String toString ="" ;
		
		if(heure == 0){
			if(minute == 0){
				toString = seconde+"s";
			}else{
				if(seconde == 0){
					toString = minute+"m";
				}else{
					toString = minute+"m"+seconde+"s";
				}
			}
		}else{
			if(minute == 0 && seconde == 0){
				toString = heure+"h";
			}else{
				toString = heure+"h"+minute+"m"+seconde+"s";
			}
		}

		return toString;
	}
}

