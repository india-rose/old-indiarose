package org.indiarose.indiarosetimer.fragment;

import org.indiarose.indiarosetimer.modele.Categorie;
import org.indiarose.indiarosetimer.modele.TimerModele;

import org.indiarose.api.fragments.core.FragmentNormal;
import org.indiarose.api.utils.LecteurVocalTTS;
import org.indiarose.lib.model.Indiagram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.indiarose.indiarosetimer.Circle;
import org.indiarose.indiarosetimer.R;
import org.indiarose.indiarosetimer.activity.MainActivity;

public class LaunchTimer extends FragmentNormal implements View.OnClickListener,OnTouchListener{

	public static final String ID_TIMER = "ID_TIMER";
	public static final String PARAMETRE ="PARAMETER";

	Circle circle;
	RelativeLayout rl ;
	View _pause;

	View _timer_frame;
	View _lock;
	View _volume;

	ImageView _consigne_timer;

	TextView _consigne_timer_text;

	TimerModele timer;

	Bundle savedInstanceState;

	Indiagram india;

	Categorie _categorie;
	int position;

	View _arrow_right;
	View _arrow_left;

	public LaunchTimer(int position,Categorie categorie){
		this._categorie = categorie;
		this.position = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(super.onCreateView(inflater, container, savedInstanceState,
				R.layout.timer)){

			recupererValues();
			ajouterVues();
			ajouterListener();
			chargerVues();
		}


		return getFragmentView();
	}

	private void chargerVues() {
		if(timer != null){

			cacherFleches();

			if(!timer.getPath_consigne().equalsIgnoreCase("")){
				_consigne_timer.setVisibility(View.VISIBLE);
				_consigne_timer_text.setVisibility(View.VISIBLE);

				india = getIndiagramManager().getIndiagramByPath(timer.getPath_consigne());

				_consigne_timer.setImageBitmap(india.getImageAsBitmap());
				_consigne_timer_text.setText(india.text);
			}else{
				_consigne_timer.setVisibility(View.GONE);
				_consigne_timer_text.setVisibility(View.GONE);
			}
		}
	}

	private void cacherFleches() {
		if(position == 0){
			_arrow_left.setVisibility(View.GONE);
		}else{
			_arrow_left.setVisibility(View.VISIBLE);
		}

		if(position == _categorie.getTimers().size()-1){
			_arrow_right.setVisibility(View.GONE);
		}else{
			_arrow_right.setVisibility(View.VISIBLE);

		}		
	}

	private void recupererValues() {
		timer = _categorie.getTimers().get(position);
	}

	protected void ajouterVues(){

		_volume = findViewById(R.id.volume);
		_arrow_right = findViewById(R.id.arrow_right);
		_arrow_left = findViewById(R.id.arrow_left);

		_timer_frame = findViewById(R.id.timer_frame);
		_consigne_timer = (ImageView) findViewById(R.id.consigne_timer);
		_consigne_timer_text = (TextView) findViewById(R.id.consigne_timer_text);

		rl = (RelativeLayout) this.findViewById(R.id.timer_frame);

		if(this.savedInstanceState == null){
			circle = new Circle(getActivity(),timer,this);
		}else{
			circle = new Circle(getActivity(), this.savedInstanceState.getFloat("pas"),
					this.savedInstanceState.getLong("firstTime"), this.savedInstanceState.getLong("pauseTime"), this.savedInstanceState.getLong("debutPause"),
					this.savedInstanceState.getBoolean("pause"),timer,this.savedInstanceState.getDouble("offset"),this);

			if(!this.savedInstanceState.getBoolean("pause"))
				changerTextPause();

		}

		rl.addView(circle);

		_pause = findViewById(R.id.button_pause);
		_lock = findViewById(R.id.lock);
	}

	protected void ajouterListener(){
		_arrow_left.setOnClickListener(this);
		_arrow_right.setOnClickListener(this);
		_pause.setOnClickListener(this);
		_lock.setOnClickListener(this);
		_timer_frame.setOnTouchListener(this);
		_consigne_timer.setOnClickListener(this);
		_volume.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.arrow_left:
			if(!circle.isLock()){
				this.onLeftSwipe();	
			}
			break;

		case R.id.arrow_right:
			if(!circle.isLock()){
				this.onRightSwipe();	
			}
			break;

		case R.id.button_pause:
			circle.changerPause();
			changerTextPause();
			break;
		case R.id.lock:
			circle.changerLock();
			changerImageLock();
			break;
		case R.id.consigne_timer:
			parlerIndia();
			break;
		case R.id.volume:
			((MainActivity)getActivity()).launchVolumeManager();			
			break;
		}
	}

	private void parlerIndia() {
		if(india != null && !circle.isLock()){
			LecteurVocalTTS ll = new LecteurVocalTTS(getActivity());
			ll.lire(india.text);
		}
	}

	private void onLeftSwipe() {
		if(position != 0){
			position -= 1;
			recupererValues();
			chargerVues();
			circle.changerTimer(timer);
		}

	}

	private void onRightSwipe() {
		if(position != _categorie.getTimers().size()){
			position += 1;

			recupererValues();
			chargerVues();
			circle.changerTimer(timer);

		}

	}

	public void changerTextPause(){
		if(circle.isPause()){
			((Button)findViewById(R.id.button_pause)).setBackgroundResource(R.drawable.selector_play);
		}else{
			((Button)findViewById(R.id.button_pause)).setBackgroundResource(R.drawable.selector_pause);
		}
	}

	public void changerImageLock(){
		if(circle.isLock()){
			((Button)findViewById(R.id.lock)).setBackgroundResource(R.drawable.selector_lock_close);
			_arrow_left.setVisibility(View.GONE);
			_arrow_right.setVisibility(View.GONE);

		}else{
			((Button)findViewById(R.id.lock)).setBackgroundResource(R.drawable.selector_lock);
			cacherFleches();
		}
	}

	@Override 
	public void onSaveInstanceState(Bundle savedInstanceStates) {
		if(circle != null){
			savedInstanceStates.putFloat("pas", circle.getPas());
			savedInstanceStates.putInt("radius", circle.getRadius());
			savedInstanceStates.putLong("firstTime", circle.getFirstTime());
			savedInstanceStates.putLong("pauseTime", circle.getPauseTime());
			savedInstanceStates.putLong("debutPause", circle.getDebutPause());
			savedInstanceStates.putBoolean("pause", circle.isPause());
			savedInstanceStates.putInt("typeChrono", circle.getTypeChrono());
			savedInstanceStates.putFloat("timeSeconde", circle.getTimeSeconde());
			savedInstanceStates.putDouble("offset", circle.getOffset());

		}
		super.onSaveInstanceState(savedInstanceStates);
	}

	/*
	public void onBackPressed(){

		super.onBackPressed();
		Intent intent = new Intent(this,Accueil.class);
		startActivity(intent);
		try{
			circle.interrupt();
			circle.onThreadStop(true);
		}catch(Exception ee){

		}
		finish();

	}
	 */

	public void onResume(){
		super.onResume();
		if(circle != null){					
			recupererValues();
			circle.setCOLOR_PRINCIPAL(timer.getColorPrincipal());
			circle.setCOLOR_SECONDAIRE(timer.getColorSecondaire());
			circle.setCOLOR_TEXT(timer.getGraduationTextColor());
			circle.setCOLOR_TRAIT(timer.getGraduationTraitColor());	
			circle.setTypeChrono(Circle.typeChronoToOffset(timer.getTypeChrono()));
			circle.invalidate();

			if(circle.isThreadPause())
				circle.redraw();
		}
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(!circle.isLock()){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN : Log.e("je passe la ", ""+MotionEvent.ACTION_DOWN); circle.handleTouchDown(event.getX(),event.getY());break;
			case MotionEvent.ACTION_MOVE : Log.e("je passe la ", ""+MotionEvent.ACTION_MOVE); circle.handleTouchMove(event.getX(), event.getY());break;
			case MotionEvent.ACTION_UP :   Log.e("je passe la ", ""+MotionEvent.ACTION_UP);circle.handleTouchUp();break;
			}
		}
		return true;
	}

	public boolean onBackPressed(){
		super.onBackPressed();
		ajouterFragment(new CategorieFragment(_categorie.getId(),0),false);
		return true;
	}
}
