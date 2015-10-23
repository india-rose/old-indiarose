package org.indiarose.indiarosetimebar.canvas.agenda;

import org.indiarose.indiarosetimebar.model.Jour;

import android.graphics.RectF;

public class ZoneJour {

	Jour jour;

	RectF rectangle;

	public ZoneJour(Jour jour, RectF rectangle) {
		this.jour = jour;
		this.rectangle = rectangle;
	}

	public Jour getJour() {
		return jour;
	}

	public void setJour(Jour jour) {
		this.jour = jour;
	}

	public RectF getRectangle() {
		return rectangle;
	}

	public void setRectangle(RectF rectangle) {
		this.rectangle = rectangle;
	}

	public boolean contient(float x, float y) {
		return rectangle.contains(x, y);
	}

}
