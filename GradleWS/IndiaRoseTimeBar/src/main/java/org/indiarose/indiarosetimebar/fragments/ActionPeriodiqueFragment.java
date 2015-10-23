package org.indiarose.indiarosetimebar.fragments;

import java.util.Calendar;
import java.util.Date;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.parametres.Parametres;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import org.indiarose.indiarosetimebar.model.Recurrence;
import org.indiarose.indiarosetimebar.model.Recurrence.FinRecurrence;
import org.indiarose.indiarosetimebar.model.Recurrence.TypeRecurrence;
/**
 * Creation d'une repetition de chronogramme, 
 * par exemple creer le meme chronogramme pour tous les mardis, 
 * ou le meme chronogramme du 15 au 20 du mois
 * @author alexandre
 *
 */
public class ActionPeriodiqueFragment extends BarreDeTempsFragmentNormal implements OnItemSelectedListener,View.OnClickListener, OnDateSetListener, TextWatcher{



	Bundle savedInstanceState = null;
	Parametres parametres;

	IcsSpinner mIcsSpinnerFinRecurrence;
	BaseAdapter mAdapterFinRecurrence;

	IcsSpinner mIcsSpinnerChoixRecurrence;
	BaseAdapter mAdapterChoixRecurrence;

	ViewGroup layoutOccurence;
	ViewGroup nbJours;
	ViewGroup nbSemaines;
	ViewGroup nbMois;
	ViewGroup nbAns;
	View choisirDate;
	ViewGroup choixjoursemaine;
	ViewGroup resumedate;

	TextView datechoisie;
	Button lundi;
	Button mardi;
	Button mercredi;
	Button jeudi;
	Button vendredi;
	Button samedi;
	Button dimanche;

	Date dateFinRecurrence;
	TypeRecurrence current_typeRecurrence;
	FinRecurrence current_finRecurrence;

	EditText serieevent;
	int nbSerieEvent;

	EditText nbJourEdit;
	EditText nbSemEdit;
	EditText nbMoisEdit;
	EditText nbAnsEdit;
	int intervalle;

	DatePickerDialog datePickerDialog = null;

	Recurrence recurrence;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (super.onCreateView(inflater, container, savedInstanceState,
				R.layout.action_periodique_layout)) {

			this.parametres = Parametres.getInstance(getActivity());
			ajouterVues();
			chargerVues();
			ajouterListener();
		}

		return getFragmentView();
	}

	private void ajouterVues() {

		mIcsSpinnerFinRecurrence = (IcsSpinner) findViewById(R.id.spinner_fin_recurrence);
		mIcsSpinnerChoixRecurrence = (IcsSpinner) findViewById(R.id.spinner_type_recurrence);
		layoutOccurence =  (ViewGroup) findViewById(R.id.occurence_avant_fin);
		choisirDate = findViewById(R.id.choisir_date);
		nbJours = (ViewGroup) findViewById(R.id.nbjours);
		nbSemaines = (ViewGroup) findViewById(R.id.nbsemaines);
		nbMois = (ViewGroup) findViewById(R.id.nbmois);
		nbAns = (ViewGroup) findViewById(R.id.nbans);
		resumedate = (ViewGroup) findViewById(R.id.resumedate);
		choixjoursemaine = (ViewGroup) findViewById(R.id.choixjourssemaine);
		lundi = (Button) choixjoursemaine.findViewById(R.id.jour_lundi);
		mardi = (Button) choixjoursemaine.findViewById(R.id.jour_mardi);
		mercredi = (Button) choixjoursemaine.findViewById(R.id.jour_mercredi);
		jeudi = (Button) choixjoursemaine.findViewById(R.id.jour_jeudi);
		vendredi = (Button) choixjoursemaine.findViewById(R.id.jour_vendredi);
		samedi = (Button) choixjoursemaine.findViewById(R.id.jour_samedi);
		dimanche = (Button) choixjoursemaine.findViewById(R.id.jour_dimanche);
		datechoisie = (TextView) resumedate.findViewById(R.id.affichedatechoisie);


		serieevent = (EditText) layoutOccurence.findViewById(R.id.nb_occurences);
		nbJourEdit = (EditText) nbJours.findViewById(R.id.nb_occurences);
		nbSemEdit = (EditText) nbSemaines.findViewById(R.id.nb_occurences);
		nbMoisEdit = (EditText) nbMois.findViewById(R.id.nb_occurences);
		nbAnsEdit = (EditText) nbAns.findViewById(R.id.nb_occurences);


	}

	private void chargerVues() {

		recurrence = new Recurrence(Calendar.getInstance().getTime(), null, TypeRecurrence.ALLDAYS, FinRecurrence.ALWAYS, 0, 1);

		mAdapterChoixRecurrence = new ArrayAdapter<String>(getActivity(), R.layout.simpletextviewforspinner, R.id.spinnerSelected, getResources().getStringArray(R.array.choixrecurrence));
		mIcsSpinnerChoixRecurrence.setAdapter(mAdapterChoixRecurrence);
		mAdapterFinRecurrence = new ArrayAdapter<String>(getActivity(), R.layout.simpletextviewforspinner,R.id.spinnerSelected,getResources().getStringArray(R.array.finrecurrence));
		mIcsSpinnerFinRecurrence.setAdapter(mAdapterFinRecurrence);
	}

	private void ajouterListener() {

		mIcsSpinnerChoixRecurrence.setOnItemSelectedListener(this);
		mIcsSpinnerFinRecurrence.setOnItemSelectedListener(this);
		choisirDate.setOnClickListener(this);
		lundi.setOnClickListener(this);
		mardi.setOnClickListener(this);
		mercredi.setOnClickListener(this);
		jeudi.setOnClickListener(this);
		vendredi.setOnClickListener(this);
		samedi.setOnClickListener(this);
		dimanche.setOnClickListener(this);

		serieevent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {


			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {
				handletextChanges(recurrence.getNbserie(), s);

			}
		});
		nbJourEdit.addTextChangedListener(this);
		nbSemEdit.addTextChangedListener(this);
		nbMoisEdit.addTextChangedListener(this);
		nbAnsEdit.addTextChangedListener(this);
	}

	@Override
	public void onItemSelected(IcsAdapterView<?> parent, View view,
			int position, long id) {
		int idparent = parent.getId();
		if(idparent == R.id.spinner_type_recurrence){

			if(getResources().getStringArray(R.array.choixrecurrence)[position].
					equals(getResources().getString(R.string.tousjours))){
				nbJours.setVisibility(View.VISIBLE);
				nbSemaines.setVisibility(View.GONE);
				nbMois.setVisibility(View.GONE);
				nbAns.setVisibility(View.GONE);
				choixjoursemaine.setVisibility(View.GONE);
				recurrence.setType(TypeRecurrence.ALLDAYS);
				recurrence.setInterval(Integer.parseInt(nbJourEdit.getText().toString()));
			}
			else if(getResources().getStringArray(R.array.choixrecurrence)[position].
					equals(getResources().getString(R.string.tousjourssemaine))){
				nbJours.setVisibility(View.GONE);
				nbSemaines.setVisibility(View.GONE);
				nbMois.setVisibility(View.GONE);
				nbAns.setVisibility(View.GONE);
				choixjoursemaine.setVisibility(View.GONE);
				recurrence.setType(TypeRecurrence.ALLDAYSOFWEEK);
				recurrence.setInterval(0);

			}
			else if(getResources().getStringArray(R.array.choixrecurrence)[position].
					equals(getResources().getString(R.string.toutessemaines))){
				nbJours.setVisibility(View.GONE);
				nbSemaines.setVisibility(View.VISIBLE);
				choixjoursemaine.setVisibility(View.VISIBLE);
				nbMois.setVisibility(View.GONE);
				nbAns.setVisibility(View.GONE);
				recurrence.setType(TypeRecurrence.ALLWEEKS);
				recurrence.setInterval(Integer.parseInt(nbSemEdit.getText().toString()));
			}
			else if(getResources().getStringArray(R.array.choixrecurrence)[position].
					equals(getResources().getString(R.string.tousmois))){
				nbJours.setVisibility(View.GONE);
				nbSemaines.setVisibility(View.GONE);
				nbMois.setVisibility(View.VISIBLE);
				nbAns.setVisibility(View.GONE);
				choixjoursemaine.setVisibility(View.GONE);
				recurrence.setType(TypeRecurrence.ALLMONTH);
				recurrence.setInterval(Integer.parseInt(nbMoisEdit.getText().toString()));
			}
			else if(getResources().getStringArray(R.array.choixrecurrence)[position].
					equals(getResources().getString(R.string.tousans))){
				nbJours.setVisibility(View.GONE);
				nbSemaines.setVisibility(View.GONE);
				nbMois.setVisibility(View.GONE);
				nbAns.setVisibility(View.VISIBLE);
				choixjoursemaine.setVisibility(View.GONE);
				recurrence.setType(TypeRecurrence.ALLYEARS);
				recurrence.setInterval(Integer.parseInt(nbAnsEdit.getText().toString()));
			}
		}
		else if(idparent == R.id.spinner_fin_recurrence){
			if(getResources().getStringArray(R.array.finrecurrence)[position].
					equals(getResources().getString(R.string.toujours))){
				layoutOccurence.setVisibility(View.GONE);
				choisirDate.setVisibility(View.GONE);
				resumedate.setVisibility(View.GONE);
				recurrence.setFin(FinRecurrence.ALWAYS);
			}
			else if(getResources().getStringArray(R.array.finrecurrence)[position].
					equals(getResources().getString(R.string.jusqua))){
				layoutOccurence.setVisibility(View.GONE);
				choisirDate.setVisibility(View.VISIBLE);
				resumedate.setVisibility(View.VISIBLE);
				recurrence.setFin(FinRecurrence.UNTIL);
			}
			else if(getResources().getStringArray(R.array.finrecurrence)[position].
					equals(getResources().getString(R.string.serieevent))){
				layoutOccurence.setVisibility(View.VISIBLE);
				choisirDate.setVisibility(View.GONE);
				resumedate.setVisibility(View.GONE);
				recurrence.setFin(FinRecurrence.OCCURENCE);
				recurrence.setNbserie(Integer.parseInt(serieevent.getText().toString()));
			}
		}

	}

	@Override
	public void onNothingSelected(IcsAdapterView<?> parent) {


	}
	@Override
	public void onClick(View v) {

		int id = v.getId();
		if(id == R.id.choisir_date){
			Calendar today = Calendar.getInstance();
			if(datePickerDialog == null){
				datePickerDialog = new DatePickerDialog(getActivity(), this, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
			}
			datePickerDialog.show();
		}
		else{
			gererBouton(v);
		}

		super.onClick(v);
	}

	@SuppressWarnings("deprecation")
	private void gererBouton(View v) {
		Button b = (Button) v;
		b.setSelected(!b.isSelected());
		//Log.d("gererbouton", b.getText()+", "+b.isSelected());
		if(b.isSelected()){
			b.setBackgroundDrawable(getResources().getDrawable(R.drawable.rond_selectionne));
			b.setTextColor(getResources().getColor(R.color.gris));
		}
		else{
			b.setBackgroundDrawable(getResources().getDrawable(R.drawable.rond_normal));
			b.setTextColor(getResources().getColor(R.color.couleur_principale));
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDateSet(DatePicker arg0, int year, int monthofyear, int dayofmonth) {

		if(datePickerDialog.isShowing()){
			datePickerDialog.dismiss();
		}
		//penser a l'offset de -1900 year
		dateFinRecurrence = new Date(year-1900, monthofyear, dayofmonth);

		datechoisie.setText(dateFinRecurrence.toLocaleString());
	}

	public boolean[] getDaysSelected(){
		boolean [] res = new boolean[7];
		res[0]=lundi.isSelected();
		res[1]=mardi.isSelected();
		res[2]=mercredi.isSelected();
		res[3]=jeudi.isSelected();
		res[4]=vendredi.isSelected();
		res[5]=samedi.isSelected();
		res[6]=dimanche.isSelected();
		return res;
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		handletextChanges(recurrence.getInterval(),arg0);
	}

	private void handletextChanges(int intervalle, Editable arg0) {

		if(arg0.toString().equals(new String(""))){
			intervalle=0;
		}
		else{
			intervalle=Integer.parseInt( arg0.toString());
			Log.e("on textchanged", "nouvel intervalle: "+intervalle);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}
}
