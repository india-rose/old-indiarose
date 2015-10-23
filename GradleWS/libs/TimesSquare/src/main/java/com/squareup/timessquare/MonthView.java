// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import java.text.DateFormat;
import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MonthView extends LinearLayout {
	TextView title;
	CalendarGridView grid;
	private Listener listener;

	/**
	 * AJOUTE -----------------------
	 */

	MonthDescriptor month;
	List<List<MonthCellDescriptor>> cells;

	public StateListDrawable getStateList(int color) {
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				new ColorDrawable(Color.parseColor("#89FF5E")));
		states.addState(new int[] { android.R.attr.state_checked },
				new ColorDrawable(Color.parseColor("#89FF5E")));
		states.addState(new int[] {}, new ColorDrawable(color));
		return states;
	}

	public boolean inDates(List<Date> dates, Date date) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
		for (Date d : dates) {
			String s1 = dateFormat.format(date);
			String s2 = dateFormat.format(d);

			if (s1.equals(s2))
				return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void afficherJours(List<Date> dates, int couleur) {
		if (cells != null) {
			final int numRows = cells.size();
			for (int i = 0; i < 6; i++) {
				CalendarRowView weekRow = (CalendarRowView) grid
						.getChildAt(i + 1);
				if (i < numRows) {
					List<MonthCellDescriptor> week = cells.get(i);
					for (int c = 0; c < week.size(); c++) {
						MonthCellDescriptor cell = week.get(c);
						CalendarCellView cellView = (CalendarCellView) weekRow
								.getChildAt(c);

						if (cellView.isSelectable
								&& cell.getDate().after(new Date())
								&& inDates(dates, cell.getDate())) {
							cellView.setJourAAfficher(true);
							// cellView.setBackgroundDrawable(getStateList(couleur));
						} else
							cellView.setJourAAfficher(false);

					}
				}
			}
		} else
			System.out.println("no cells");
	}

	/**
	 * -----------------------
	 */

	public static MonthView create(ViewGroup parent, LayoutInflater inflater,
			DateFormat weekdayNameFormat, Listener listener, Calendar today) {
		final MonthView view = (MonthView) inflater.inflate(R.layout.month,
				parent, false);

		final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);

		int firstDayOfWeek = today.getFirstDayOfWeek();
		final CalendarRowView headerRow = (CalendarRowView) view.grid
				.getChildAt(0);
		for (int offset = 0; offset < 7; offset++) {
			today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
			final TextView textView = (TextView) headerRow.getChildAt(offset);
			textView.setText(weekdayNameFormat.format(today.getTime()));

		}
		today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
		view.listener = listener;
		return view;
	}

	public MonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		title = (TextView) findViewById(R.id.title);
		grid = (CalendarGridView) findViewById(R.id.calendar_grid);
	}

	public void init(MonthDescriptor month,
			List<List<MonthCellDescriptor>> cells, boolean displayOnly) {

		this.cells = cells;
		this.month = month;

		// Logr.d("Initializing MonthView (%d) for %s",
		// System.identityHashCode(this), month);
		long start = System.currentTimeMillis();

		String mois = month.getLabel();
		mois = String.valueOf(mois.charAt(0)).toUpperCase()
				+ mois.subSequence(1, mois.length());

		title.setText(mois);
		title.setTextColor(getContext().getResources().getColor(
				R.color.calendar_couleur_texte));

		final int numRows = cells.size();
		grid.setNumRows(numRows);
		for (int i = 0; i < 6; i++) {
			CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
			weekRow.setListener(listener);
			if (i < numRows) {
				weekRow.setVisibility(VISIBLE);
				List<MonthCellDescriptor> week = cells.get(i);
				for (int c = 0; c < week.size(); c++) {
					MonthCellDescriptor cell = week.get(c);
					CalendarCellView cellView = (CalendarCellView) weekRow
							.getChildAt(c);

					cellView.setText(Integer.toString(cell.getValue()));
					cellView.setEnabled(cell.isCurrentMonth());
					cellView.setClickable(!displayOnly);

					cellView.setSelectable(cell.isSelectable());
					cellView.setSelected(cell.isSelected());
					cellView.setCurrentMonth(cell.isCurrentMonth());
					cellView.setToday(cell.isToday());
					cellView.setRangeState(cell.getRangeState());
					cellView.setHighlighted(cell.isHighlighted());
					cellView.setTag(cell);
				}
			} else {
				weekRow.setVisibility(GONE);
			}
		}
		// Logr.d("MonthView.init took %d ms", System.currentTimeMillis() -
		// start);
	}

	public interface Listener {
		void handleClick(MonthCellDescriptor cell);
	}
}
