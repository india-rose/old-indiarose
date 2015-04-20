package org.indiarose.indiarosetimebar.views.core;

import android.view.View;

/**
 * Contient toutes les foncitons qui doivent �tre implementees par les elements de d'une listview
 * @author florentchampigny
 */
public interface IListElementView {

	public void ajouterVues();

	public void remplirVues();

	public void ajouterListeners();

	public void reCharger(View view);
}
