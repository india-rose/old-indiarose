package org.indiarose.indiarosetimebar.canvas.core;

import org.indiarose.indiarosetimebar.canvas.thread.ThreadDessin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.*;

/**
 * Extension de SurfaceView permettant un decoupage simple, et une gestion des threads de fa�on autonome
 * @author florentchampigny
 */
public abstract class ZoneDessin extends SurfaceView implements
		SurfaceHolder.Callback, View.OnTouchListener {

	ThreadDessin thread;
	Paint paint;
	Canvas canvas;
	RectF rectangleDessin = new RectF();

	public ZoneDessin(Context context, boolean modifiable) {
		super(context);
		getHolder().addCallback(this);
		this.thread = new ThreadDessin(getHolder(), this);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		if (modifiable)
			setOnTouchListener(this);
	}

	/**
	 * A appeller apres la creation, fait le lien entre la taille de l'ecran, et les objets qu'il doit creer
	 */
	public abstract void calculerMeilleurTaille();

	/**
	 * Remet le canvas tout a blanc
	 */
	public void clear(Canvas canvas) {
		if (canvas != null)
			canvas.drawColor(Color.WHITE);
	}

	public void onDraw(Canvas canvas) {
		setCanvas(canvas);
		getPaint().setAntiAlias(true);
		getPaint().setStrokeWidth(2);
	}

	/**
	 * Demande le redessin du canvas
	 */
	public void reDraw() {
		this.invalidate();
		this.thread.draw();
	}

	public void setSurfaceDessinable(float left, float top, float right,
			float bottom) {
		getRectangleDessin().set(left, top, right, bottom);
	}

	public boolean appartient(float x, float y) {
		return getRectangleDessin().contains(x, y);
	}

	public void click(float x, float y) {
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (appartient(event.getX(), event.getY())) {
				click(event.getX(), event.getY());
			}
		}
		return true;
	}

	// -----------------------------SURFACE
	// HOLDER-------------------------------

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		try {
			thread.draw();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			thread.start();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		thread.interrupt();
	}

	// -----------------------------GETTERS AND
	// SETTERS----------------------------
	public ThreadDessin getThread() {
		return thread;
	}

	public void setThread(ThreadDessin thread) {
		this.thread = thread;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public RectF getRectangleDessin() {
		return rectangleDessin;
	}

	public void setRectangleDessin(RectF rectangleDessin) {
		this.rectangleDessin = rectangleDessin;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

}
