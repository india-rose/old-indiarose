package org.indiarose.indiarosetimebar.calendrier.core;

import java.util.*;

import org.indiarose.indiarosetimebar.calendrier.CalendrierAndroidTimesSquare;
import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.parametres.Parametres;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Fournit une abstraction des vues calendriers, dans le but de pouvoir y integrer n'importe quelle librairie / affichage
 *
 */
public abstract class Calendrier {

	/**
	 * Delegate appelle lors de la selection de dates
	 */
	public interface DateChoisieDelegate {
		public void onDateChoisie(List<Date> selectedDates);
	}

	private List<Date> selectedDates; //Liste des dates selectionnees par l'utilisateur

	private DateChoisieDelegate dateChoisieDelegate = null;
	private ViewGroup viewGroup; //La vue contenant le calendrier
	private Context context; //Le context de l'application
	private Parametres parametres; //Les parametres de l'application ChronoGram

	Calendar dateDeDebut; //La date a partir de laquelle afficher le calendrier
	Calendar dateDeFin; //La date jusqu'a laquelle afficher le calendrier

	public Calendrier(Context context, DateChoisieDelegate delegate,
			ViewGroup layout) {
		this.viewGroup = layout;
		this.context = context;
		this.dateChoisieDelegate = delegate;
		this.parametres = Parametres.getInstance(getContext());

		dateDeDebut = Calendar.getInstance();
		dateDeFin = Calendar.getInstance();
		dateDeFin.add(Calendar.MONTH, Parametres.getInstance(context)
				.getIntervalleCalendrierMois());
	}

	public static Calendrier getInstance(Context context,
			DateChoisieDelegate delegate, ViewGroup layout) {
		return new CalendrierAndroidTimesSquare(context, delegate, layout);
		// return new CalendrierAndroidCalendarCard(context, delegate, layout);
	}

	public DateChoisieDelegate getDateChoisieDelegate() {
		return dateChoisieDelegate;
	}

	public void setDateChoisieDelegate(DateChoisieDelegate dateChoisieDelegate) {
		this.dateChoisieDelegate = dateChoisieDelegate;
	}

	public ViewGroup getViewGroup() {
		return viewGroup;
	}

	public void setViewGroup(ViewGroup viewGroup) {
		this.viewGroup = viewGroup;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Parametres getParametres() {
		return parametres;
	}

	public void setParametres(Parametres parametres) {
		this.parametres = parametres;
	}

	public List<Date> getSelectedDates() {
		return selectedDates;
	}

	public void setSelectedDates(List<Date> selectedDates) {
		this.selectedDates = selectedDates;
	}

	public Calendar getDateDeDebut() {
		return dateDeDebut;
	}

	public void setDateDeDebut(Calendar dateDeDebut) {
		this.dateDeDebut = dateDeDebut;
	}

	public Calendar getDateDeFin() {
		return dateDeFin;
	}

	public void setDateDeFin(Calendar dateDeFin) {
		this.dateDeFin = dateDeFin;
	}

	/**
	 * Demande a ses implementations d'afficher les jours donnees en parametres differemment
	 * @param jours
	 * @param couleurJours
	 */
	public abstract void afficherJours(List<Jour> jours, int couleurJours);

	public void majSelectedDates(List<Date> dates) {
		selectedDates = dates;
		this.dateChoisieDelegate.onDateChoisie(selectedDates);
	}
}
