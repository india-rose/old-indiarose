package com.wt.calendarcard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.wt.calendar_card.R;

public class CalendarCard extends RelativeLayout {

	private TextView cardTitle;
	private int itemLayout = R.layout.card_item_simple;
	private OnItemRender mOnItemRender;
	private OnItemRender mOnItemRenderDefault;
	private OnCellItemClick mOnCellItemClick;
	private Calendar dateDisplay;
	private ArrayList<CheckableLayout> cells = new ArrayList<CheckableLayout>();
	private LinearLayout cardGrid;

	/**
	 * AJOUTE -------------------------
	 */
	List<Date> datesAColorier = new ArrayList<Date>();
	int couleurDates = Color.YELLOW;
	int couleurAujourdHui = Color.parseColor("#7AAFFF");
	boolean refresh = true;

	public boolean isToday(Date date) {
		return inDates(Arrays.asList(new Date()), date);
	}

	public StateListDrawable getStateList(int color) {
		StateListDrawable states = new StateListDrawable();
		states.addState(
				new int[] { android.R.attr.state_pressed },
				new ColorDrawable(getContext().getResources().getColor(
						R.color.bleu_fonce)));
		states.addState(
				new int[] { android.R.attr.state_checked },
				new ColorDrawable(getContext().getResources().getColor(
						R.color.bleu_fonce)));
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

	public void afficherDates(List<Date> dates, int couleur) {
		this.datesAColorier = dates;
		this.couleurDates = couleur;
		couleurAujourdHui = getContext().getResources().getColor(
				R.color.vert_fonce);
		if (refresh) {
			updateCells();
			refresh = !refresh;
		}
	}

	public void refresh() {
		refresh = true;
		updateCells();
		refresh = false;
	}

	/**
	 * -------------------------
	 */

	public CalendarCard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CalendarCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CalendarCard(Context context) {
		super(context);
		init(context);
	}

	@SuppressLint("NewApi")
	private void init(Context ctx) {
		if (isInEditMode())
			return;
		View layout = LayoutInflater.from(ctx).inflate(R.layout.card_view,
				null, false);

		if (dateDisplay == null)
			dateDisplay = Calendar.getInstance();

		cardTitle = (TextView) layout.findViewById(R.id.cardTitle);
		cardGrid = (LinearLayout) layout.findViewById(R.id.cardGrid);

		cardTitle
		.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
		.format(dateDisplay.getTime()));

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		((TextView) layout.findViewById(R.id.cardDay1)).setText(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		((TextView) layout.findViewById(R.id.cardDay2)).setText(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		((TextView) layout.findViewById(R.id.cardDay3)).setText(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		((TextView) layout.findViewById(R.id.cardDay4)).setText(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		((TextView) layout.findViewById(R.id.cardDay5)).setText(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		((TextView) layout.findViewById(R.id.cardDay6)).setText(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()));
		cal.add(Calendar.DAY_OF_WEEK, 1);
		((TextView) layout.findViewById(R.id.cardDay7)).setText(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.getDefault()));

		LayoutInflater la = LayoutInflater.from(ctx);
		for (int y = 0; y < cardGrid.getChildCount(); y++) {
			LinearLayout row = (LinearLayout) cardGrid.getChildAt(y);
			for (int x = 0; x < row.getChildCount(); x++) {
				CheckableLayout cell = (CheckableLayout) row.getChildAt(x);

				cell.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (CheckableLayout c : cells)
							c.setChecked(false);
						((CheckableLayout) v).setChecked(true);

						if (getOnCellItemClick() != null)
							getOnCellItemClick().onCellClick(v,
									(CardGridItem) v.getTag()); // TODO create
						// item
					}
				});

				View cellContent = la.inflate(itemLayout, cell, false);
				cell.addView(cellContent);
				cells.add(cell);
			}
		}

		addView(layout);

		mOnItemRenderDefault = new OnItemRender() {
			@Override
			public void onRender(CheckableLayout v, CardGridItem item) {
				((TextView) v.getChildAt(0)).setText(item.getDayOfMonth()
						.toString());
			}
		};

		updateCells();
	}

	private int getDaySpacing(int dayOfWeek) {
		if (Calendar.SUNDAY == dayOfWeek)
			return 6;
		else
			return dayOfWeek - 2;
	}

	private int getDaySpacingEnd(int dayOfWeek) {
		return 8 - dayOfWeek;
	}

	private void updateCells() {
		Calendar cal;
		Integer counter = 0;
		if (dateDisplay != null)
			cal = (Calendar) dateDisplay.clone();
		else
			cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_MONTH, 1);

		int daySpacing = getDaySpacing(cal.get(Calendar.DAY_OF_WEEK));

		if (daySpacing > 0) {
			Calendar prevMonth = (Calendar) dateDisplay.clone();
			prevMonth.add(Calendar.MONTH, -1);
			prevMonth.set(Calendar.MONTH, prevMonth.get(Calendar.MONTH));

			prevMonth.set(Calendar.DAY_OF_MONTH,
					prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
			// Log.wtf("calendarcard- updatecell month",
			// "" + prevMonth.get(Calendar.MONTH));
			// Log.wtf("calendarcard- updatecell nbdays",
			// "" + prevMonth.getMaximum(Calendar.DAY_OF_MONTH));
			int lastDayOfPrevMonth = prevMonth
					.getActualMaximum(Calendar.DAY_OF_MONTH);/* +1 */// the +1 if
			// too much
			for (int i = lastDayOfPrevMonth - daySpacing + 1; i < lastDayOfPrevMonth + 1; i++) {
				CheckableLayout cell = cells.get(counter);
				cell.setTag(new CardGridItem(i).setEnabled(false));
				cell.setEnabled(false);
				
				try{
					((TextView) cell.getChildAt(0)).setTextColor(Color.GRAY);
					}catch(Exception e){}
				
				(mOnItemRender == null ? mOnItemRenderDefault : mOnItemRender)
				.onRender(cell, (CardGridItem) cell.getTag());
				counter++;
			}
		}

		int firstDay = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		int lastDay = cal.get(Calendar.DAY_OF_MONTH) + 1;

		for (int i = firstDay; i < lastDay; i++) {
			cal.set(Calendar.DAY_OF_MONTH, i - 1);
			Calendar date = (Calendar) cal.clone();
			date.add(Calendar.DAY_OF_MONTH, 1);
			CheckableLayout cell = cells.get(counter);
			cell.setTag(new CardGridItem(i).setEnabled(true).setDate(date));
			
			try{
				((TextView) cell.getChildAt(0)).setTextColor(Color.GRAY);
				}catch(Exception e){}

			/**
			 * AJOUTE ----------------
			 */

			if (refresh) {
				if (isToday(date.getTime())) {
					cell.setBackgroundDrawable(getStateList(couleurAujourdHui));
					((TextView) cell.getChildAt(0)).setTextColor(Color.WHITE);
				} else {
					// DESACTIVER LES DATES ANTERIEURES A AUJOURDHUI
					if (new Date().compareTo(date.getTime()) >= 0) {
						cell.setEnabled(false);
						cell.setBackgroundColor(Color.parseColor("#F0F0F0"));
						((TextView) cell.getChildAt(0)).setTextColor(Color.GRAY);
						((TextView) cell.getChildAt(0)).setTypeface(null, Typeface.BOLD);
					} else {

						if (inDates(this.datesAColorier, date.getTime())) {
							cell.setBackgroundDrawable(getStateList(couleurDates));
							((TextView) cell.getChildAt(0)).setTextColor(Color.WHITE);
							((TextView) cell.getChildAt(0)).setTypeface(null, Typeface.BOLD);
						} else {
							cell.setBackgroundDrawable(getStateList(Color.WHITE));
							((TextView) cell.getChildAt(0)).setTextColor(Color.GRAY);
							((TextView) cell.getChildAt(0)).setTypeface(null, Typeface.BOLD);
						}

						cell.setEnabled(true);
					}

				}
			}

			/**
			 * ----------------
			 */

			cell.setVisibility(View.VISIBLE);
			(mOnItemRender == null ? mOnItemRenderDefault : mOnItemRender)
			.onRender(cell, (CardGridItem) cell.getTag());
			counter++;
		}

		if (dateDisplay != null)
			cal = (Calendar) dateDisplay.clone();
		else
			cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));

		daySpacing = getDaySpacingEnd(cal.get(Calendar.DAY_OF_WEEK));

		if (daySpacing > 0) {
			for (int i = 0; i < daySpacing; i++) {
				CheckableLayout cell = cells.get(counter);
				cell.setTag(new CardGridItem(i + 1).setEnabled(false)); // .setDate((Calendar)cal.clone())
				cell.setEnabled(false);
				cell.setBackgroundColor(Color.parseColor("#F0F0F0"));
				
				try{
				((TextView) cell.getChildAt(0)).setTextColor(Color.GRAY);
				}catch(Exception e){}
				
				cell.setVisibility(View.VISIBLE);
				(mOnItemRender == null ? mOnItemRenderDefault : mOnItemRender)
				.onRender(cell, (CardGridItem) cell.getTag());
				counter++;
			}
		}

		if (counter < cells.size()) {
			for (int i = counter; i < cells.size(); i++) {
				cells.get(i).setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && cells.size() > 0) {
			int size = (r - l) / 7;
			for (CheckableLayout cell : cells) {
				cell.getLayoutParams().height = size;
			}
		}
	}

	public int getItemLayout() {
		return itemLayout;
	}

	public void setItemLayout(int itemLayout) {
		this.itemLayout = itemLayout;
		// mCardGridAdapter.setItemLayout(itemLayout);
	}

	public OnItemRender getOnItemRender() {
		return mOnItemRender;
	}

	public void setOnItemRender(OnItemRender mOnItemRender) {
		this.mOnItemRender = mOnItemRender;
		// mCardGridAdapter.setOnItemRender(mOnItemRender);
	}

	public Calendar getDateDisplay() {
		return dateDisplay;
	}

	public void setDateDisplay(Calendar dateDisplay) {
		this.dateDisplay = dateDisplay;
		String mois = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
		.format(dateDisplay.getTime());
		mois = String.valueOf(mois.charAt(0)).toUpperCase()
				+ mois.subSequence(1, mois.length());
		cardTitle.setText(mois);

		cardTitle.setTextColor(Color.WHITE);
		cardTitle.setTypeface(null, Typeface.BOLD);
	}

	public OnCellItemClick getOnCellItemClick() {
		return mOnCellItemClick;
	}

	public void setOnCellItemClick(OnCellItemClick mOnCellItemClick) {
		this.mOnCellItemClick = mOnCellItemClick;
	}

	/**
	 * call after change any input data - to refresh view
	 */
	public void notifyChanges() {
		// mCardGridAdapter.init();
		updateCells();
	}

}
