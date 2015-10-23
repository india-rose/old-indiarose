package org.indiarose.indiarosetimebar.canvas.thread;

import org.indiarose.indiarosetimebar.canvas.core.ZoneDessin;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Thread s'occupant du dessin d'une zone de dessin
 * @author florentchampigny
 *
 */
@SuppressLint("WrongCall")
public class ThreadDessin extends Thread{

	private SurfaceHolder surfaceHolder;
	private ZoneDessin zoneDessin;
	private Canvas canvas = null;
	
	public ThreadDessin(SurfaceHolder surfaceHolder, ZoneDessin zoneDessin) {
		this.surfaceHolder = surfaceHolder;
		this.zoneDessin = zoneDessin;
	}

	public void run() {
		//while (true) {
			draw();
		//}
	}
	
	public void draw(){
		try {
			canvas = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {
				
				zoneDessin.clear(canvas);
				zoneDessin.onDraw(canvas);
				
			}
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}
}
