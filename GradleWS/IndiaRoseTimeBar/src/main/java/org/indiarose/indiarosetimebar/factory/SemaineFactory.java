package org.indiarose.indiarosetimebar.factory;

import java.util.Date;

import java.util.List;

import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.model.Semaine;
import org.indiarose.indiarosetimebar.utils.DateManager;

import android.content.Context;

@Deprecated
/**
 * Classe permettant de g�n�rer une semaine
 * @author florentchampigny
 *
 */
public class SemaineFactory {

	boolean anglaise = false;
	Context context;

	public SemaineFactory(Context context) {
		this.context = context;
	}

	public SemaineFactory(Context context, boolean anglaise) {
		this(context);
		this.anglaise = anglaise;
	}

	public Semaine genererSemaine() {

		List<Date> semaineDate = DateManager.recupererSemaineCourrante();

		Jour lundi = new Jour(semaineDate.get(0));
		Jour mardi = new Jour(semaineDate.get(1));
		Jour mercredi = new Jour(semaineDate.get(2));
		Jour jeudi = new Jour(semaineDate.get(3));
		Jour vendredi = new Jour(semaineDate.get(4));
		Jour samedi = new Jour(semaineDate.get(5));
		Jour dimanche = new Jour(semaineDate.get(6));

		Semaine semaine = new Semaine();

		if (anglaise)
			semaine.addJours(dimanche, lundi, mardi, mercredi, jeudi, vendredi,
					samedi);
		else
			semaine.addJours(lundi, mardi, mercredi, jeudi, vendredi, samedi,
					dimanche);

		return semaine;
	}
}
