package org.indiarose.indiarosetimer.thread;

import org.indiarose.indiarosetimer.Circle;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread{

	private SurfaceHolder surfaceHolder;
	private Circle circle;
	private Canvas canvas;

	private boolean stop  = false;
	private boolean pause = false;
	private boolean redraw = false;

	public MainThread(SurfaceHolder surfaceHolder, Circle circle) {
		this.surfaceHolder = surfaceHolder;
		this.circle = circle;
	}


	public void run() {
		canvas = null;

		while (true) {
			if(stop)
				return;

			if(!pause || redraw){
				redraw = false;
				draw();
			}

		}
	}


	public void draw(){
		try {
			canvas = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {

				circle.clearCircle(canvas);
				circle.moveCircle();
				circle.onDraw(canvas);

			}
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void redraw(){
		redraw = true;
	}
	
	public void onPause(boolean pause){
		this.pause = pause;
	}
	
	public void onStop(boolean stop){
		this.stop = stop;
	}
	
	public boolean isPause(){
		return this.pause;
	}
}
