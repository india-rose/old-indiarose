package org.indiarose.indiarosetimer.views;


import org.indiarose.indiarosetimer.R;

import org.indiarose.indiarosetimer.modele.Categorie;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CategorieView extends View implements View.OnTouchListener{

	Categorie categorie;
	Context context;
	ImageView image;
	View view;
	TextView titre;
	int position ;
	
	public CategorieView(Categorie categorie, Context context, View view,
			int position) {

		super(context);
		this.categorie = categorie;
		this.context = context;
		this.view = view;
		this.position = position;

		ajouterVues();
		remplirVues();
		ajouterListeners();

	}

	public void ajouterListeners() {
		view.setOnTouchListener(this);
	}

	public void remplirVues() {

		titre.setText(categorie.getIndiagram().text);
		if(position == 0)
			image.setImageResource(R.drawable.plustache);
		else
			image.setImageBitmap(categorie.getIndiagram().getImageAsBitmap());
	}

	public void ajouterVues() {
		image = (ImageView) view.findViewById(R.id.image);
		titre = (TextView) view.findViewById(R.id.titre);	
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
