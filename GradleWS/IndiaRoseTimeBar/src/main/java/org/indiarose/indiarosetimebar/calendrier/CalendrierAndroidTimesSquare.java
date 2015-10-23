package org.indiarose.indiarosetimebar.calendrier;

import java.util.Date;
import java.util.List;

import org.indiarose.indiarosetimebar.calendrier.core.Calendrier;
import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.utils.JourManager;

import android.content.Context;
import android.view.ViewGroup;

import com.squareup.timessquare.*;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class CalendrierAndroidTimesSquare extends Calendrier {

	private CalendarPickerView calendar;

	public CalendrierAndroidTimesSquare(Context context,
			DateChoisieDelegate delegate, ViewGroup layout) {

		super(context, delegate, layout);

		calendar = new CalendarPickerView(context, null);
		calendar.init(getDateDeDebut().getTime(), getDateDeFin().getTime())
				.inMode(SelectionMode.MULTIPLE);

		getViewGroup().removeAllViews();
		getViewGroup().addView(calendar);

		ajouterListener();

	}

	protected void ajouterListener() {
		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				majSelectedDates(calendar.getSelectedDates());
			}

			@Override
			public void onDateSelected(Date date) {
				majSelectedDates(calendar.getSelectedDates());

			}
		});
	}

	@Override
	public void afficherJours(List<Jour> jours, int couleur) {
		calendar.afficherJours(JourManager.joursToDates(jours), couleur);
	}

}
