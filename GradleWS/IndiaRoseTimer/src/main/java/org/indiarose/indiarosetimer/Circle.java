package org.indiarose.indiarosetimer;
import org.indiarose.indiarosetimer.fragment.*;
import org.indiarose.indiarosetimer.thread.MainThread;

import org.indiarose.indiarosetimer.modele.TimerModele;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

@SuppressLint("ViewConstructor")
public class Circle extends SurfaceView implements SurfaceHolder.Callback{

	public static final float TEXT_SIZE = 20 ;
	public static final int CHRONO_RIGHT = 1;
	public static final int CHRONO_LEFT = 2;
	public static final int TIME_ALARME = 10000; 
	public static final int TIME_VIBRATE = 2500;

	private Context ctx;
	private int COLOR_PRINCIPAL;
	private int COLOR_SECONDAIRE;
	private int COLOR_TEXT;
	private int COLOR_TRAIT;

	private int typeChrono;
	private float timeSeconde;	
	private float pas;
	private int radius;

	private MainThread thread;
	LaunchTimer launch;
	RectF rect = new RectF();
	private Paint paint;	
	private Canvas _canvas=null;

	public long firstTime;
	private boolean pause = false;	
	private long pauseTime = 0;
	private long debutPause;


	private double offset;
	private long currentTime;
	private int canvasHeight;
	private int canvasWidth;

	private boolean lock;

	public Circle(Context context, float pas, long firstTime, long pauseTime, long debutPause,boolean pause,TimerModele timer,double offset,LaunchTimer launch){

		super(context);
		this.ctx = context;
		this.launch = launch;		
		this.firstTime = firstTime;
		this.pauseTime = pauseTime;
		this.debutPause = debutPause;
		this.pause = pause;
		getHolder().addCallback(this);
		this.thread = new MainThread(getHolder(), this);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.typeChrono = typeChronoToOffset(timer.getTypeChrono());
		this.timeSeconde = timer.getTimeSeconde();
		COLOR_PRINCIPAL = timer.getColorPrincipal();
		COLOR_SECONDAIRE= timer.getColorSecondaire();
		COLOR_TEXT = timer.getGraduationTextColor();
		COLOR_TRAIT = timer.getGraduationTraitColor();
		this.offset=offset;
		calculerBestRadius();
	}

	public Circle(Context context,TimerModele timer,LaunchTimer launch) {
		super(context);
		this.ctx = context;
		this.launch = launch;
		this.pas = 0;
		getHolder().addCallback(this);
		this.thread = new MainThread(getHolder(), this);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		firstTime = System.currentTimeMillis();
		this.typeChrono = typeChronoToOffset(timer.getTypeChrono());
		this.timeSeconde = timer.getTimeSeconde() ;
		COLOR_PRINCIPAL = timer.getColorPrincipal();
		COLOR_SECONDAIRE= timer.getColorSecondaire();
		COLOR_TEXT = timer.getGraduationTextColor();
		COLOR_TRAIT = timer.getGraduationTraitColor();
		calculerBestRadius();
		changerPause();
	}

	public void changerTimer(TimerModele timer){
		//this.pas = 0;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		firstTime = System.currentTimeMillis();
		this.typeChrono = typeChronoToOffset(timer.getTypeChrono());
		this.timeSeconde = timer.getTimeSeconde() ;
		COLOR_PRINCIPAL = timer.getColorPrincipal();
		COLOR_SECONDAIRE= timer.getColorSecondaire();
		COLOR_TEXT = timer.getGraduationTextColor();
		COLOR_TRAIT = timer.getGraduationTraitColor();
		calculerBestRadius();
		pause = false;
		restartTimer();
		this.invalidate();
	}
	
	private void restartTimer(){
		pauseTime = 0;
		debutPause = 0;
		firstTime = System.currentTimeMillis();	
		offset = 0;
		changerPause();
		((Activity)(ctx)).runOnUiThread(new Runnable() {

			public void run() {
				launch.changerTextPause();
			}
		});

		thread.onPause(false);
		
	}


	public Circle(Context context, TimerModele timer,
			Bundle values, LaunchTimer launch) {

		this(context,values.getFloat("pas"),
				values.getLong("firstTime"), 
				values.getLong("pauseTime"),
				values.getLong("debutPause"),
				values.getBoolean("pause"),
				timer,
				values.getDouble("offset"),launch);

		if(!values.getBoolean("pause"))
			this.pauseTime = values.getLong("pauseTime") + System.currentTimeMillis() - values.getLong("debutPause");

	}

	public static int typeChronoToOffset(int type){
		int offset = 0;	
		switch (type) {
		case CHRONO_LEFT:
			offset = -1 ;
			break;

		case CHRONO_RIGHT:
			offset = 1;
			break;
		}

		return offset;
	}

	@SuppressWarnings("deprecation")
	protected void calculerBestRadius(){

		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			this.radius = display.getWidth()/2 - 50;
		else
			this.radius = display.getHeight()/2 -150;

	}

	private void alarme(){

		try {
			AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
			int volume = audio.getStreamVolume(AudioManager.STREAM_ALARM);
			if(volume <= 5){
				Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(TIME_VIBRATE);

			}else{
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
				Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
				r.play();
				Thread.sleep(TIME_ALARME);
				r.stop();
			}
		} catch (Exception e) {}
	}

	public void moveCircle() {

		if(!pause){
			currentTime = (long) (System.currentTimeMillis() - firstTime - pauseTime);
		}else{
			currentTime = (long) (debutPause - firstTime - pauseTime);
		}

		pas = (int)((currentTime / 1000)-offset) * (360/timeSeconde);

		if(pas == 360 && !pause){
			new AlarmeTask().execute();
			thread.onPause(true);
		}

		if(pas < 0)
			pas +=360;

		if(pas > 360 && !pause)
			pas = 360;
	}

	public void onDraw(Canvas canvas) {

		this._canvas=canvas;

		rect.set(getWidth()/2 - radius, getHeight()/2 - radius, getWidth()/2 + radius, getHeight()/2 + radius);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);

		if(canvas != null){

			canvasHeight = _canvas.getHeight()/2;
			canvasWidth = _canvas.getWidth()/2;

			paint.setColor(COLOR_PRINCIPAL);
			canvas.drawCircle(canvasWidth, canvasHeight, radius, paint);

			paint.setColor(COLOR_SECONDAIRE);


			canvas.drawArc (rect, -90,typeChrono*pas, true, paint);

			// Le temps afficher
			drawGraduation();

			// Centre du timer
			paint.setColor(Color.BLACK);
			canvas.drawCircle(canvasWidth, canvasHeight, 5, paint);

			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(canvasWidth, canvasHeight, radius, paint);
			paint.setStyle(Paint.Style.FILL);
		}
	}


	private void drawGraduation() {


		paint.setTextSize(TEXT_SIZE);		
		paint.setTextAlign(Paint.Align.CENTER);

		for(int i =0;i <timeSeconde ;i+=timeSeconde/12){
			paint.setColor(COLOR_TEXT);
			_canvas.drawText(formatTime(i), canvasWidth + (float)Math.cos(Math.toRadians(-1*typeChrono*i*(360/timeSeconde)-90))*(radius+TEXT_SIZE), canvasHeight + (float)Math.sin(Math.toRadians(-1*typeChrono*i*(360/timeSeconde)-90))*(radius+TEXT_SIZE), paint);
			mydrawLineWithAngle(typeChrono*i*(360/timeSeconde), 40);
		}



		if(timeSeconde <= 5*60.0f){
			myDrawLines(10, 30);
			myDrawLines(5, 20);
			myDrawLines(1, 10);
		}
		else{
			if(timeSeconde <= 12*60.0f){
				myDrawLines(60, 30);
				myDrawLines(10, 20);
				myDrawLines(5, 10);
			}
			else{
				if(timeSeconde <= 60*60.0f){
					myDrawLines(5*60, 30);
					myDrawLines(60, 20);
					myDrawLines(10, 10);
				}
			}
		}
	}


	private void myDrawLines(int intervale, int size) {
		paint.setColor(COLOR_TRAIT);
		for(int j=0;j<timeSeconde;j+=intervale)
			mydrawLineWithAngle(typeChrono*j*(360/timeSeconde), size);			

	}

	private void mydrawLineWithAngle(float angle, int size){
		paint.setColor(COLOR_TRAIT);
		_canvas.drawLine(canvasWidth+(float)Math.cos(Math.toRadians(angle-90))*(radius), canvasHeight+(float)Math.sin(Math.toRadians(angle-90))*(radius), canvasWidth+(float)Math.cos(Math.toRadians(angle-90))*(radius-size), canvasHeight+(float)Math.sin(Math.toRadians(angle-90))*(radius-size), paint);
	}

	public void clearCircle(Canvas canvas) {
		if(canvas != null)
			canvas.drawColor(Color.WHITE);
	}

	public void changerPause(){

		if(pause){
			pauseTime += System.currentTimeMillis() - debutPause;
		}else{
			debutPause = System.currentTimeMillis();
		}

		pause = !pause;
	}

	public void changerLock(){
		lock = !lock;
	}



	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		if(!thread.isAlive()){
			thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		thread.interrupt();
	}

	public String formatTime(float seconds){

		StringBuffer sbf = new StringBuffer();
		int nbHours = (int) (seconds/3600);
		float remaining = seconds-(nbHours*3600);
		int nbMinutes = (int) (remaining/60);
		int nbSeconds = (int) (seconds-(nbHours*3600)-(nbMinutes*60));

		if(nbHours>0)
			sbf.append(nbHours+"h");

		if(nbMinutes>0)
			sbf.append(nbMinutes+"'");

		if((nbHours <= 1 && nbSeconds > 0) || (nbHours == 0 && nbMinutes == 0))
			sbf.append(nbSeconds+"\"");

		return sbf.toString();
	}

	public void handleMoveEvent(float _x, float _y){
		Log.e("Handle Move Event", _x + " , " + _y);
		if(isInCircle(_x,_y)){

			Point p1 = new Point((int)_x, (int)_y);
			double angle ;

			angle= Math.toDegrees(Math.acos(mycos(p1,new Point(canvasWidth, canvasHeight-radius),new Point(canvasWidth, canvasHeight))));

			if(typeChrono*p1.x < typeChrono*canvasWidth)
				angle = 360-angle;

			changeAngle(angle);
		}

	}

	private void changeAngle(double angle) {
		offset+=(float) ((pas-(int)angle)*timeSeconde / 360);
	}

	private boolean isInCircle(float _x, float _y) {		
		float a = _x-canvasWidth;
		float b = _y-canvasHeight;

		if(Math.sqrt((a*a)+(b*b))<= radius)
			return true;

		return false;
	}

	private double mycos(Point p1, Point p2, Point p3) {

		return (
				((p1.x-p3.x)*(p2.x-p3.x))+((p1.y-p3.y)*(p2.y-p3.y)))
				/
				(
						(Math.sqrt(Math.pow(p1.x-p3.x, 2)+Math.pow(p1.y-p3.y, 2)))
						*(Math.sqrt(Math.pow(p2.x-p3.x, 2)+Math.pow(p2.y-p3.y, 2))
								)
						);
	}


	private class AlarmeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			alarme();
			restartTimer();
			return null;
		}
	}

	long time = 0;
	public void handleTouchMove(float x, float y) {
		if(System.currentTimeMillis() - time > 50){
			time = System.currentTimeMillis();
			this.handleMoveEvent(x, y);
		}
	}

	private boolean wasInPause = false;
	private boolean passedByDown = false;

	public void handleTouchUp() {
		if(passedByDown){
			passedByDown=false;
			if(!wasInPause && this.isPause())
				this.changerPause();
		}
	}

	public void handleTouchDown(float x, float y) {
		passedByDown=true;
		wasInPause = this.isPause();

		if(!this.isPause())
			this.changerPause();
	}

	public boolean isLock(){
		return lock;
	}


	public void interrupt(){
		this.thread.interrupt();
	}

	public void redraw(){
		this.thread.redraw();
	}

	public boolean isThreadPause(){
		return this.thread.isPause();
	}
	public void onThreadPause(boolean pause){
		this.thread.onPause(pause);
	}

	public void onThreadStop(boolean stop){
		this.thread.onStop(stop);
	}

	public float getPas() {
		return pas;
	}

	public int getRadius() {
		return radius;
	}

	public Paint getPaint() {
		return paint;
	}

	public MainThread getThread() {
		return thread;
	}

	public RectF getRect() {
		return rect;
	}

	public long getFirstTime() {
		return firstTime;
	}

	public boolean isPause() {
		return pause;
	}

	public long getPauseTime() {
		return pauseTime;
	}

	public long getDebutPause() {
		return debutPause;
	}

	public int getTypeChrono() {
		return typeChrono;
	}

	public void setTypeChrono(int type){
		this.typeChrono = type;
	}
	public float getTimeSeconde() {
		return timeSeconde;
	}

	public double getOffset(){
		return offset;
	}

	public int getCOLOR_PRINCIPAL() {
		return COLOR_PRINCIPAL;
	}

	public void setCOLOR_PRINCIPAL(int cOLOR_PRINCIPAL) {
		COLOR_PRINCIPAL = cOLOR_PRINCIPAL;
	}

	public int getCOLOR_SECONDAIRE() {
		return COLOR_SECONDAIRE;
	}

	public void setCOLOR_SECONDAIRE(int cOLOR_SECONDAIRE) {
		COLOR_SECONDAIRE = cOLOR_SECONDAIRE;
	}

	public int getCOLOR_TEXT() {
		return COLOR_TEXT;
	}

	public void setCOLOR_TEXT(int cOLOR_TEXT) {
		COLOR_TEXT = cOLOR_TEXT;
	}

	public int getCOLOR_TRAIT() {
		return COLOR_TRAIT;
	}

	public void setCOLOR_TRAIT(int cOLOR_TRAIT) {
		COLOR_TRAIT = cOLOR_TRAIT;
	}

	public void setTimeSeconde(float timeSeconde) {
		this.timeSeconde = timeSeconde;
	}

	public Canvas get_canvas() {
		return _canvas;
	}

	public void set_canvas(Canvas _canvas) {
		this._canvas = _canvas;
	}
}
