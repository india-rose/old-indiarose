package org.indiarose.net;

import java.util.List;

import org.codegist.crest.CRest;
import org.indiarose.lib.ActionLog;

public class NetLogger {
	public static final String urlEnvoie="index.php";
	public static String envoyer(List<ActionLog> logs){ 
//		return Net.requete( urlEnvoie, 
//				Net.construireDonnes( "email",email, "logs",json ) 
//				); 
//		}
		CRest crest = CRest.getInstance();
		LoggerService logservice = crest.build(LoggerService.class);
		return logservice.envoyer(logs);
	}		
		
	
}
