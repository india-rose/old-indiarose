package org.indiarose.indiarosetimebar.persistance.json;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import org.indiarose.indiarosetimebar.model.Donnees;
import org.indiarose.indiarosetimebar.persistance.Persistance;
import org.indiarose.indiarosetimebar.utils.JourManager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class JSonPersistance extends Persistance {

	public static String dossierJson = "/IndiaRoseChronoGram/";
	public static String fichierJson = dossierJson + "donnees.json";
	private File dossier = null;
	private File fichier = null;

	public JSonPersistance(Context context) {
		super(context);
		try {
			dossier = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + dossierJson);
			fichier = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + fichierJson);
			if (!dossier.exists()) {
				System.out.println(dossier.toString() + " n existe pas");
				dossier.mkdir();
			}
			if (!fichier.exists()) {
				System.out.println(fichier.toString() + " n existe pas");
				fichier.createNewFile();
			}
		} catch (Exception e) {
			fichier = null;
			e.printStackTrace();

		}
	}

	@Override
	public Donnees chargerDonnees() {
		Donnees donnees = null;
		if (fichier != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				donnees = mapper.readValue(fichier, Donnees.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (donnees == null) {
			donnees = new Donnees();
			sauvegarder(donnees);
		}

		Log.e("CHARGEMENT", donnees.toString());

		return donnees;
	}

	@Override
	public void sauvegarder(Donnees donnees) {
		Log.e("SAUVEGARDE", donnees.toString());
		
		donnees.setJours(JourManager.joursAGarder(donnees.getJours(), 1));
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(fichier, donnees);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
