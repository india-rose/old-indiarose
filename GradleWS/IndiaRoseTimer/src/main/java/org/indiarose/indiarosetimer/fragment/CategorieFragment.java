package org.indiarose.indiarosetimer.fragment;

import org.indiarose.indiarosetimer.modele.Categorie;
import org.indiarose.indiarosetimer.modele.TimerModele;

import org.indiarose.api.fragments.core.FragmentNormal;

import org.indiarose.indiarosetimer.Circle;
import org.indiarose.indiarosetimer.R;
import org.indiarose.indiarosetimer.activity.MainActivity;

import org.indiarose.indiarosetimer.database.base.AccessBaseTimer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CategorieFragment extends FragmentNormal implements View.OnTouchListener{

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private View central_view;

	private int position_liste;
	private int id_categorie;

	private Categorie _categorie;

	private ImageView image;
	private TextView text_description;

	View _arrow_right;
	View _arrow_left;

	View play ;
	
	View _setting_timer;
	View _delete_timer;
	
	public CategorieFragment(int id_categorie,int id_position_liste){
		this.id_categorie = id_categorie;
		this.position_liste = id_position_liste;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(super.onCreateView(inflater, container, savedInstanceState,
				R.layout.categorie_timer)){

			ajouterVues();
			chargerVues();
			ajouterListener();
		}

		return getFragmentView();
	}

	private void ajouterListener() {
		central_view.setOnTouchListener(this);
		image.setOnClickListener(this);
		_arrow_left.setOnClickListener(this);
		_arrow_right.setOnClickListener(this);
		_setting_timer.setOnClickListener(this);
		play.setOnClickListener(this);
		_delete_timer.setOnClickListener(this);
		
		gestureDetector = new GestureDetector(new SwipeGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
	}

	@Override
	public void onClick(View v){
		if(v.getId() == R.id.arrow_right){
			this.onRightSwipe();
		}else{

			if(v.getId() == R.id.arrow_left){
				this.onLeftSwipe();	
			}else{
				if(v.getId() == R.id.image){
					if(position_liste == 0)
					ajouterFragment(new ParameterTimer(id_categorie,_categorie.getTimers().size()),false);
					
				}else{
					if(v.getId() == R.id.play){
						ajouterFragment(new LaunchTimer(position_liste-1,_categorie), false);
					}else{
						if(v.getId() == R.id.volume){
							((MainActivity)getActivity()).launchVolumeManager();
						}else{
							if(v.getId() == R.id.settings){
								
							}else{
								if(v.getId() == R.id.setting_timer){
									ajouterFragment(new ParameterTimer(id_categorie,_categorie.getTimers().get(position_liste-1).getId(),position_liste), false);
								}else{
									if(v.getId() == R.id.delete_timer){
										deleteTimer();
									}
								}
							}
						}
					}
				}
			}						
		}					
	}

	private void deleteTimer() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle(getString(R.string.deleteText));
		// set dialog message
		alertDialogBuilder
		.setMessage(getString(R.string.delete_question))
		.setCancelable(true)
		.setPositiveButton(getString(R.string.okText),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				AccessBaseTimer db = new AccessBaseTimer(getActivity());
				db.open();
				db.deleteTimer(_categorie.getTimers().get(position_liste-1).getId(),id_categorie);
				db.close();
				position_liste = 0;
				chargerVues();
			}
		})
		.setNegativeButton(getString(R.string.annuler),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	private void onLeftSwipe() {
		if(position_liste != 0){
			position_liste -= 1;
			chargerVues();
		}

	}

	private void onRightSwipe() {
		if(position_liste != _categorie.getTimers().size()){
			position_liste += 1;
			chargerVues();
		}

	}

	private void chargerVues() {
		AccessBaseTimer db = new AccessBaseTimer(getActivity());
		db.open();
		_categorie = db.getCategory(id_categorie, getIndiagramManager());
		db.close();

		if(position_liste == 0){
			_arrow_left.setVisibility(View.GONE);
			_setting_timer.setVisibility(View.GONE);
			_delete_timer.setVisibility(View.GONE);
			image.setImageResource(R.drawable.vide);
			text_description.setText(getString(R.string.new_timer));			
		}else{
			_setting_timer.setVisibility(View.VISIBLE);
			_arrow_left.setVisibility(View.VISIBLE);
			_delete_timer.setVisibility(View.VISIBLE);

		}

		if(position_liste == _categorie.getTimers().size()){
			_arrow_right.setVisibility(View.GONE);
		}else{
			_arrow_right.setVisibility(View.VISIBLE);

		}

		if(_categorie == null || _categorie.getTimers().size() == 0){
			play.setVisibility(View.GONE);
		}else{
			play.setVisibility(View.VISIBLE
					);

		}
		
		if(position_liste != 0){
			TimerModele t = _categorie.getTimers().get(position_liste-1);
			if(t.getTypeChrono() == Circle.CHRONO_LEFT){
				image.setImageResource(R.drawable.left);
			}else{
				image.setImageResource(R.drawable.right);
			}
			text_description.setText(t.getName());
		}

	}

	@SuppressWarnings("deprecation")
	private void ajouterVues() {
		central_view = findViewById(R.id.central_view);
		image = (ImageView) findViewById(R.id.image);
		text_description = (TextView)findViewById(R.id.text_description);
		_arrow_right = findViewById(R.id.arrow_right);
		_arrow_left = findViewById(R.id.arrow_left);
		play = findViewById(R.id.play);
		_setting_timer = findViewById(R.id.setting_timer);
		_delete_timer = findViewById(R.id.delete_timer);
	}

	private class SwipeGestureDetector extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 50;
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				float diffAbs = Math.abs(e1.getY() - e2.getY());
				float diff = e1.getX() - e2.getX();

				if (diffAbs > SWIPE_MAX_OFF_PATH)
					return false;

				// Left swipe
				if (diff > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					CategorieFragment.this.onRightSwipe();
				} 
				// Right swipe
				else if (-diff > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					CategorieFragment.this.onLeftSwipe();
				}
			} catch (Exception e) {
				Log.e("Home", "Error on gestures");
			}
			return false;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onBackPressed(){
		super.onBackPressed();
		ajouterFragment(new GridCategoryFragment(),false);
		return true;
	}
}

