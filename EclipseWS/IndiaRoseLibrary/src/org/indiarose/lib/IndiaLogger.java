package org.indiarose.lib;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.indiarose.R;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.net.NetLogger;

import android.R.integer;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class IndiaLogger {

	public static final String PATH = Environment.getExternalStorageDirectory() + File.separator;
	public static final String DIRPATH = PATH + "IndiaRose/Log";	
	public static final String FILEPREFIX = "log_";
	public static final String FILEEXTENSION=".json";
	private static ArrayList<ActionLog> lesActions = new ArrayList<ActionLog>();
	public static int seuilFlush=1000;
	public Activity a ;
	public AlertDialog.Builder builder;


	protected static void writeToXml(String data){
		
	}

	public static void writeAllSentence(List<Indiagram> listIndia){
	
	}

	public static void writeCorrections(List<Indiagram> lastList,List<Indiagram> afterList){
	
	}

	public static void addActionLog(Context currentContext, String description, String type, long timestamp){
		AccountManager manager = (AccountManager) currentContext.getSystemService(Context.ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();
		String gmail=null;
		for(Account account: list)
		{
			if(account.type.equalsIgnoreCase("com.google"))
			{
				gmail = account.name;
				break;
			}
		}
		ActionLog a = new ActionLog(description, type, ""+timestamp,gmail);
		lesActions.add(a);
		System.out.println("Ã§a ajoute action : size =" + lesActions.size() );
		control(a);


	}

	private static void control(ActionLog a){
		if(lesActions.size() > seuilFlush && a.getType().equals(ActionLog.TYPE_VALIDER)){
			write();
			System.out.println("ca flush");
			removeAllAction();
		}
	}

	private static void removeAllAction() {
		lesActions.clear();
		if(!lesActions.isEmpty()){
			System.out.println("IndiaLogger - removeAllAction fail");
		}

	}

	private static void write() {
		
		String filename = findNextFile();
		File f = new File(DIRPATH, filename);
		if(!f.exists()){
			try {
				boolean b =f.createNewFile();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		try{
			mapper.writeValue(new File(DIRPATH, filename), lesActions);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private static String findNextFile() {
		int res= 0;

		File f= new File(DIRPATH);
		//check que c'est bien un dir
		if(!f.isDirectory() || !f.exists()){
			System.out.println("INDIALOGGER - dirpath pas directory");
			new File(DIRPATH).mkdirs();
		}
		f=new File(DIRPATH);
		if(f.list().length >0){
			//recupeï¿½ï¿½re la liste des fichiers
			String [] lesFiles = f.list();
			for(String s : lesFiles){
				System.out.println("INDIALOGGER - "+s);
				//on recupï¿½ï¿½re le int contenu dans le nom de fichier
				//rappel les fichiers se nomment : log_rank.json
				String rank  = s.substring(s.lastIndexOf("_")+1, s.lastIndexOf(FILEEXTENSION));
				System.out.println("INDIALOGGER - rank  = "+rank);
				//on fait monter
				if(res < Integer.parseInt(rank)){
					res = Integer.parseInt(rank);
				}
			}
			res++; // on veux Ã©crire dnas un nouveau fichier donc on fais monter le rank de 1.
			System.out.println(new String(FILEPREFIX+res+FILEEXTENSION));
			return new String(FILEPREFIX+res+FILEEXTENSION);
		}
		else{
			//pas de file de log, on crï¿½ï¿½er le premier
			return new String(FILEPREFIX+0+FILEEXTENSION);
		}
	}

	/**
	 * dans cette mÃ©thode on cherche la derniÃ¨re balise valider pour sauvegarder le plus de phrases non flush
	 * et ensuite on les Ã©crit dans le fichier Ã  l'aide de write
	 */
	public static void flushB4Save(){
		int nbActions = lesActions.size();
		if (nbActions != 0){
			int index = nbActions;
			boolean found=false;
			while(!found && index >=1){
				index--;

				if(lesActions.get(index).getType().equals(ActionLog.TYPE_VALIDER)){ //si l'action est un type valider, c'est la fin d'une phrase
					found=true;
				}
			}
			if(found){
				//on a trouvÃ© une fin de phrase Ã  save, Ã  l'index index
				//on recupere toutes les actions du debut jusqu'a index
				//la derniÃ¨re phrase valide
				ArrayList<ActionLog> res =new ArrayList<ActionLog>();
				for(int i = 0; i < index+1;i++){
					res.add(lesActions.get(i));
				}
				//on definit lesActions Ã  sauvegardÃ© avec la nouvelle liste construite
				lesActions= res;
				write();
				removeAllAction();
			}
		}

	}

	private static final long tailleMaxLogs=1024*1024 ;// la taille d'1Mo en bytes 
	public static void rmLogsIfTooHuge(){
		System.out.println("indialogger - directory size : "+ dirSize(new File(DIRPATH)));
		if(dirSize(new File(DIRPATH))>tailleMaxLogs){

			//trop de logs on efface
			File f= new File(DIRPATH);
			if(f.exists()){
				if(f.isDirectory()){
					for(String s : f.list()){
						DeleteRecursive(new File(DIRPATH,s));
					}
				}
			}
		}
	}
	private static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}
	/**
	 * Return the size of a directory in bytes
	 */
	private static long dirSize(File dir) {

		if (dir.exists()) {
			long result = 0;
			File[] fileList = dir.listFiles();
			for(int i = 0; i < fileList.length; i++) {
				// Recursive call if it's a directory
				if(fileList[i].isDirectory()) {
					result += dirSize(fileList [i]);
				} else {
					// Sum the file size in bytes
					result += fileList[i].length();
				}
			}
			return result; // return the file size
		}
		return 0;
	}


	public void sendLogs(Activity act){
		this.a=act;
		builder = new AlertDialog.Builder(act);
		builder.setMessage(act.getResources().getString(R.string.logAlertMessage));
		builder.setTitle(act.getResources().getString(R.string.logAlertTitle));
		builder.setPositiveButton(act.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				sendlogV2(a);
			}
		});
		
		builder.setNegativeButton(a.getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
			
				AlertDialog alertDialog = builder.create();		
				alertDialog.show();
			}
		});
		
		
	}
	
	public static boolean sendingLogs = false;
	public static NetLoggerAsyncTaskV2 async = null;
	
	public static ProgressDialog mDialog;
	@SuppressWarnings("deprecation")
	public void sendlogV2(Activity act){
	//creer la progress et ensuite lancer l'async
		this.a=act;
		mDialog = new ProgressDialog(a);
		mDialog.setMessage(a.getResources().getString(R.string.logProgressMessage));
		mDialog.setTitle(a.getResources().getString(R.string.logProgressTitle));
		mDialog.setCancelable(false);
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		
		
		File f = new File(DIRPATH);
		if(!f.exists() || f == null){
			System.out.println("indialogger - sendlog1");
		}
		else{
			mDialog.setMax(f.list().length);
		}
		mDialog.setProgress(0);
		mDialog.setButton(a.getResources().getString(R.string.stopSending), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//on interompt l'envoi
				if(async != null){
					async.cancel(true);
					mDialog.dismiss();
				}
			}
		});
		
		mDialog.show();
		while(!mDialog.isShowing()){
			
		}
		async = new NetLoggerAsyncTaskV2();
		async.execute(a);
		
	}
	
	class NetLoggerAsyncTaskV2 extends AsyncTask<Activity, Void, Void>{
		private Activity act = null;
		private String currentFile;
		private int nbFiles=0;
		private int current=0;
		@Override
		protected Void doInBackground(Activity... params) {
			try{
			this.act = params[0];
			File f = new File(DIRPATH);
			if(!f.exists() || f == null){
				System.out.println("indialogger - sendlog1");
			}
			else{
				//f exists
				
				ConnectivityManager connMgr = (ConnectivityManager) 
						a.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					// fetch data
					nbFiles  = f.list().length;
					current = 0;
					for(String currentFile : f.list()){
						if(isCancelled()){
							break;
						}
						if(f.list().length !=0){
							
							File logfile = new File(DIRPATH,currentFile);
//							System.out.println("IndiaLogger - sendlog2 "+logfile);
							
							//System.out.println("indialogger-gmail : "+gmail);
							ObjectMapper mapper= new ObjectMapper();
							try {
								ArrayList<ActionLog> lesAct = mapper.readValue(new File(DIRPATH, currentFile), new TypeReference<ArrayList<ActionLog>>(){});
								
								//System.out.println(json);
								//Toast.makeText(a, "envoi du fichier "+current+" sur "+nbFiles+". Ne pas quitter", Toast.LENGTH_LONG).show();
								String s =NetLogger.envoyer(lesAct);
								//int sendStatus = Integer.parseInt(""+s.charAt(0));
								Log.d("async - ", s/*+" i : "+sendStatus*/ );
//								if(sendStatus == 1){
//									DeleteRecursive(new File(DIRPATH, currentFile));
//									current++;
//									mDialog.setProgress(current);
//									if(current == nbFiles){
//										//le dossier est vide, on dismiss
//										mDialog.dismiss();
//									}
//								}
								
								
							} catch (Exception e) {
								
								e.printStackTrace();
							}
						}
						else{
							Toast.makeText(a, a.getResources().getString(R.string.noLog), Toast.LENGTH_SHORT);
						}
					}

				} else {
					Toast.makeText(a, a.getResources().getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
				}

			}
			return null;
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			Log.e("IndiaLogger Async", "cancelled");
			super.onCancelled();
		}
		
		
		
	}



}
