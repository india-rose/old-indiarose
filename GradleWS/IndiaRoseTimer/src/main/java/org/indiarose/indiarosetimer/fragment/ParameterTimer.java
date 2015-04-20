package org.indiarose.indiarosetimer.fragment;

import org.indiarose.indiarosetimer.modele.TimerModele;

import org.indiarose.api.fragments.SelectionnerIndiagramFragment;
import org.indiarose.api.fragments.SelectionnerIndiagramFragment.SelectionnerIndiagramDelegate;
import org.indiarose.api.fragments.core.FragmentNormal;
import org.indiarose.lib.model.Indiagram;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import org.indiarose.indiarosetimer.R;
import org.indiarose.indiarosetimer.date.FormaterDate;

import org.indiarose.indiarosetimer.database.base.AccessBaseTimer;

public class ParameterTimer extends FragmentNormal implements View.OnClickListener,SelectionnerIndiagramDelegate{

	View style_timer;
	View timerSeconde_timer;
	View graduation_text_color;
	View graduation_trait_color;
	View color_principal;
	View color_secondaire;

	private boolean active = false;
	private static String [] items ;


	private TextView second_value;
	private TextView minute_value;
	private TextView hour_value;
	private View nom;

	private int graduationTextColor;
	private int graduationTraitColor;
	private int colorPrincipal;
	private int colorSecondaire;
	private int typeChono;

	private View india_consigne;
	private String filePath;

	private int seconde;
	private int minute;
	private int heure;
	View valider;
	private int idTimer;
	private TimerModele timer;
	private int id_categorie;
	private int id_timer = -1;
	private int position_liste;
	public ParameterTimer(int id_categorie,int position_liste){
		this.id_categorie = id_categorie;
		this.position_liste = position_liste;
	}

	public ParameterTimer(int id_categorie,int id_timer,int position_liste){
		this.id_categorie = id_categorie;
		this.id_timer = id_timer;	
		this.position_liste = position_liste;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(super.onCreateView(inflater, container, savedInstanceState,
				R.layout.parameter_timer)){
			ajouterVues();
			recupererValues();
			if(timer == null){
				initialiserParametres();
			}else{
				reinitialiserParametres();
			}
			ajouterListeners();
		}

		return getFragmentView();

	}

	private void reinitialiserParametres(){

		graduationTextColor = timer.getGraduationTextColor();
		graduation_text_color.findViewById(R.id.color).setBackgroundColor(graduationTextColor);
		graduationTraitColor = timer.getGraduationTraitColor();
		graduation_trait_color.findViewById(R.id.color).setBackgroundColor(graduationTraitColor);
		colorPrincipal = timer.getColorPrincipal();
		color_principal.findViewById(R.id.color).setBackgroundColor(colorPrincipal);
		colorSecondaire = timer.getColorSecondaire();
		color_secondaire.findViewById(R.id.color).setBackgroundColor(colorSecondaire);
		typeChono = timer.getTypeChrono()-1;	
		initialiserItems();
		formatTime(timer.getTimeSeconde());
		((TextView)style_timer.findViewById(R.id.tiret)).setText(items[typeChono]);
		((EditText)nom.findViewById(R.id.value)).setText(timer.getName());

		if(!timer.getPath_consigne().equalsIgnoreCase(""))
			changerImageIndia(getIndiagramManager().getIndiagramByPath(timer.getPath_consigne()));

	}

	private void initialiserParametres() {
		graduationTextColor = Color.BLACK;
		graduationTraitColor = Color.BLACK;
		colorPrincipal = Color.parseColor("#2B568D");
		colorSecondaire = Color.parseColor("#7CCC6D");	
		typeChono = 0;
		initialiserItems();
	}

	private void initialiserItems(){
		items = new String[2];
		items[0]=getString(R.string.chrono_droit);
		items[1]=getString(R.string.chrono_gauche);
	}

	private void recupererValues() {
		if(id_timer != -1){
			AccessBaseTimer db = new AccessBaseTimer(getActivity());
			db.open();
			timer = db.getTimer(id_timer);
			db.close();		 
		}
	}

	private void ajouterVues() {
		style_timer = this.findViewById(R.id.style_timer);
		timerSeconde_timer = this.findViewById(R.id.timerSeconde_timer);
		graduation_text_color = this.findViewById(R.id.graduation_color_text);
		color_principal = this.findViewById(R.id.color_principale);
		color_secondaire = this.findViewById(R.id.color_secondaire);
		graduation_trait_color = this.findViewById(R.id.graduation_color_trait);
		valider = this.findViewById(R.id.valider_parameter_timer);
		nom = this.findViewById(R.id.name_timer);
		india_consigne = this.findViewById(R.id.indiagram_consigne);
	}

	private void ajouterListeners() {
		style_timer.setOnClickListener(this);
		timerSeconde_timer.setOnClickListener(this);
		graduation_text_color.setOnClickListener(this);
		color_principal.setOnClickListener(this);
		color_secondaire.setOnClickListener(this);
		graduation_trait_color.setOnClickListener(this);
		valider.setOnClickListener(this);
		nom.setOnClickListener(this);
		india_consigne.setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.style_timer:
			changerStyleTimer();
			break;

		case R.id.timerSeconde_timer:
			changerDuree();
			break;

		case R.id.graduation_color_text:
			changerTextGraduationColor();
			break;

		case R.id.color_principale:
			changerCouleurPrincipale();
			break;
		case R.id.color_secondaire:
			changerCouleurSecondaire();
			break;
		case R.id.graduation_color_trait:
			changerTraitGraduationColor();
			break;
		case R.id.valider_parameter_timer:
			enregistrerTimer();
			break;

		case R.id.indiagram_consigne:
			ajouterFragment(new SelectionnerIndiagramFragment(this),true);
			break;
		}
	}

	public int getTimeSeconde(){
		return seconde + minute * 60 + heure * 3600;
	}

	private void enregistrerTimer() {
		AccessBaseTimer db = new AccessBaseTimer(getActivity());
		int timeSeconde = getTimeSeconde();
		long idLastInsert  = 0 ;
		if(timeSeconde > 0){
			if(id_timer == -1){
				db.open();
				db.insertTimer(new TimerModele(typeChono+1,timeSeconde, graduationTextColor, graduationTraitColor, colorPrincipal, colorSecondaire,((EditText)nom.findViewById(R.id.value)).getText().toString(),filePath));
				idLastInsert = db.returnLastTimer();
				db.insertContient((int)idLastInsert, id_categorie);
				db.close();
				ajouterFragment(new CategorieFragment(id_categorie,position_liste+1),false);
			}else{
				db.open();
				db.uptadeTimer(new TimerModele(id_timer,typeChono+1,timeSeconde, graduationTextColor, graduationTraitColor, colorPrincipal, colorSecondaire,((EditText)nom.findViewById(R.id.value)).getText().toString(),filePath));
				idLastInsert = idTimer;
				db.close();	
				ajouterFragment(new CategorieFragment(id_categorie,position_liste),false);
			}

		}else{
			Toast.makeText(getActivity(), "Choisissez une durée pour le timer", Toast.LENGTH_SHORT).show();
		}

	}

	private void changerCouleurPrincipale() {
		if(!active){
			active = true;
			final ColorPickerDialog colorDialog = new ColorPickerDialog(getActivity(), colorPrincipal);

			colorDialog.setAlphaSliderVisible(false);
			colorDialog.setTitle(getString(R.string.chooseColor));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active = false;
					colorPrincipal = colorDialog.getColor();
					color_principal.findViewById(R.id.color).setBackgroundColor(colorPrincipal);
				}
			});

			colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active=false;
					//Nothing to do here.
				}
			});

			colorDialog.show();
		}

	}

	private void changerCouleurSecondaire() {
		if(!active){
			active = true;
			final ColorPickerDialog colorDialog = new ColorPickerDialog(getActivity(),colorSecondaire);

			colorDialog.setAlphaSliderVisible(false);
			colorDialog.setTitle(getString(R.string.chooseColor));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active = false;
					colorSecondaire = colorDialog.getColor();
					color_secondaire.findViewById(R.id.color).setBackgroundColor(colorSecondaire);
				}
			});

			colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active=false;
					//Nothing to do here.
				}
			});

			colorDialog.show();
		}

	}



	private void changerTextGraduationColor() {
		if(!active){
			active = true;
			final ColorPickerDialog colorDialog = new ColorPickerDialog(getActivity(), graduationTextColor);

			colorDialog.setAlphaSliderVisible(false);
			colorDialog.setTitle(getString(R.string.chooseColor));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active = false;
					graduationTextColor = colorDialog.getColor();
					graduation_text_color.findViewById(R.id.color).setBackgroundColor(graduationTextColor);;
				}
			});

			colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active=false;
				}
			});

			colorDialog.show();
		}
	}

	private void changerTraitGraduationColor() {
		if(!active){
			active = true;
			final ColorPickerDialog colorDialog = new ColorPickerDialog(getActivity(), graduationTraitColor);

			colorDialog.setAlphaSliderVisible(false);
			colorDialog.setTitle(getString(R.string.chooseColor));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active = false;
					graduationTraitColor = colorDialog.getColor();
					graduation_trait_color.findViewById(R.id.color).setBackgroundColor(graduationTraitColor);;
				}
			});

			colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active=false;
				}
			});

			colorDialog.show();
		}
	}

	private void changerStyleTimer() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle(getString(R.string.type));
		// set dialog message
		alertDialogBuilder
		.setSingleChoiceItems(items, typeChono, null)
		.setCancelable(false)
		.setPositiveButton(getString(R.string.valider),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				typeChono = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
				((TextView)style_timer.findViewById(R.id.tiret)).setText(items[typeChono]);

			}
		})
		.setNegativeButton(getString(R.string.annuler),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	private void changerDuree() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = this.getLayoutInflater();
		View v = inflater.inflate(R.layout.seekbar_dialog, null);
		second_value = (TextView) v.findViewById(R.id.value_second);
		minute_value = (TextView) v.findViewById(R.id.value_minute);
		hour_value = (TextView) v.findViewById(R.id.value_hour);
		builder.setView(v)
		.setPositiveButton(getString(R.string.valider), new 
				DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				formatTime(Integer.parseInt(second_value.getText().toString())+Integer.parseInt(minute_value.getText().toString())*60+Integer.parseInt(hour_value.getText().toString())*3600);
			}
		})
		.setNegativeButton(getString(R.string.annuler),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		})
		.setTitle(getString(R.string.duree));

		SeekBar seekbar_second = (SeekBar)v.findViewById(R.id.seekbar_second);
		seekbar_second.setMax(100);
		seekbar_second.setProgress(0);
		seekbar_second.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				if(Integer.parseInt(minute_value.getText().toString()) != 0
						|| Integer.parseInt(hour_value.getText().toString()) != 0
						){
					second_value.setText(""+(progress*59/100));
				}else{
					second_value.setText(""+(progress*60/100));

				}
			}
		});

		SeekBar seekbar_minute = (SeekBar)v.findViewById(R.id.seekbar_minute);
		seekbar_minute.setMax(100);
		seekbar_minute.setProgress(0);
		seekbar_minute.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				if(Integer.parseInt(second_value.getText().toString()) != 0
						|| Integer.parseInt(hour_value.getText().toString()) != 0
						){
					minute_value.setText(""+(progress*59/100));
				}else{
					minute_value.setText(""+(progress*60/100));

				}			
			}
		});

		SeekBar seekbar_hour = (SeekBar)v.findViewById(R.id.seekbar_hour);
		seekbar_hour.setMax(100);
		seekbar_hour.setProgress(0);
		seekbar_hour.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				if(Integer.parseInt(second_value.getText().toString()) != 0
						|| Integer.parseInt(minute_value.getText().toString()) != 0
						){
					hour_value.setText(""+(progress*23/100));
				}else{
					hour_value.setText(""+(progress*24/100));

				}			
			}
		});
		builder.create();
		builder.show();
	}

	private void formatTime(float timeSeconde) {
		int [] time = FormaterDate.formaterDate(timeSeconde);
		heure = time[FormaterDate.HEURE];
		minute = time[FormaterDate.MINUTE];
		seconde = time[FormaterDate.SECONDE];
		((TextView)timerSeconde_timer.findViewById(R.id.tiret)).setText(FormaterDate.timeToString(heure, minute, seconde));		
	}


	@Override
	public void onIndiagramSelected(Indiagram indiagram) {
		if(indiagram != null){
			changerImageIndia(indiagram);
		}
	}

	private void changerImageIndia(Indiagram indiagram) {
		filePath = indiagram.filePath;

		if(indiagram.text != null)
			((TextView)india_consigne.findViewById(R.id.titre)).setText(indiagram.text);

		if(indiagram.getImageAsBitmap() != null)
			((ImageView)india_consigne.findViewById(R.id.image)).setImageBitmap(indiagram.getImageAsBitmap());

	}

	@Override
	public boolean onBackPressed(){
		super.onBackPressed();
		ajouterFragment(new CategorieFragment(id_categorie,position_liste),false);


		return true;
	}
}
