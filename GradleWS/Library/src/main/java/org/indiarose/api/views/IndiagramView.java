package org.indiarose.api.views;

import org.indiarose.library.R;
import org.indiarose.api.views.core.ListElementView;
import org.indiarose.lib.model.Indiagram;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class IndiagramView extends ListElementView {

	public interface IndiagramViewClickDelegate {
		public void onIndiagramViewClick(Indiagram indiagram);
	}

	ImageView image;
	TextView titre;
	IndiagramViewClickDelegate delegate;

	public IndiagramView(Object objet, Context context, View view,
			int position, IndiagramViewClickDelegate delegate) {
		super(objet, context, view, position);
		this.delegate = delegate;
	}

	@Override
	public void ajouterVues() {
		image = (ImageView) getView().findViewById(R.id.image);
		titre = (TextView) getView().findViewById(R.id.titre);
	}

	@Override
	public void afficherNormal() {
		getView().setBackgroundColor(Color.BLACK);
		titre.setTextColor(Color.WHITE);
	}

	@Override
	public void afficherTouch() {
		getView().setBackgroundColor(Color.WHITE);
		titre.setTextColor(Color.BLACK);
	}

	@Override
	public void onClick(View v) {
		if (getObjet() instanceof Indiagram) {
			delegate.onIndiagramViewClick((Indiagram) getObjet());
		}
		afficherNormal();
	}

	@Override
	public void remplirVues() {

		if (getObjet() instanceof Indiagram) {
			Indiagram indiagram = (Indiagram) getObjet();
			if (indiagram != null) {
				if (indiagram.text != null)
					titre.setText(indiagram.text);

				image.setImageBitmap(indiagram.getImageAsBitmap());
			}
		}

	}

}
