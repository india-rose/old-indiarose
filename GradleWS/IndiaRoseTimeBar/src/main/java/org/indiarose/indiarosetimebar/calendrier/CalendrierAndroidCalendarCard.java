package org.indiarose.indiarosetimebar.calendrier;

import java.util.*;

import org.indiarose.indiarosetimebar.calendrier.core.Calendrier;
import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.utils.JourManager;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.wt.calendarcard.*;

public class CalendrierAndroidCalendarCard extends Calendrier {
	private CalendarCardPager calendarPager;

	private List<CalendarCard> calendriers = new LinkedList<CalendarCard>();

	private List<Jour> jours = new ArrayList<Jour>();
	private int couleurJours = Color.YELLOW;

	public CalendrierAndroidCalendarCard(final Context context,
			DateChoisieDelegate delegate, final ViewGroup layout) {

		super(context, delegate, layout);

		calendarPager = new CalendarCardPager(getContext());
		calendarPager.setAdapter(new MyCardPagerAdapter(getContext()));

		getViewGroup().removeAllViews();
		getViewGroup().addView(calendarPager);

	}

	private class MyCardPagerAdapter extends CardPagerAdapter {

		public MyCardPagerAdapter(Context ctx) {
			super(ctx);
		}

		@Override
		public Object instantiateItem(View collection, final int position) {
			CalendarCard calendrier = (CalendarCard) super.instantiateItem(
					collection, position);

			calendriers.add(calendrier);

			calendrier.setOnCellItemClick(new OnCellItemClick() {

				@Override
				public void onCellClick(View v, CardGridItem item) {
					List<Date> l = new ArrayList<Date>();
					l.add(item.getDate().getTime());
					majSelectedDates(l);
				}
			});

			calendrier.afficherDates(JourManager.joursToDates(jours),
					couleurJours);

			return calendrier;
		}
	}

	@Override
	public void afficherJours(List<Jour> jours, int couleur) {
		this.jours.addAll(jours);
		this.couleurJours = couleur;
		for (CalendarCard calendrier : calendriers) {
			calendrier.afficherDates(JourManager.joursToDates(jours),
					couleurJours);
			calendrier.refresh();
		}
	}
}
