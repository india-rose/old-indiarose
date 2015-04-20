// Copyright 2013 Square, Inc.

package com.squareup.timessquare;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.squareup.timessquare.MonthCellDescriptor.RangeState;

public class CalendarCellView extends TextView {
	private static final int[] STATE_SELECTABLE = { R.attr.state_selectable };
	private static final int[] STATE_CURRENT_MONTH = { R.attr.state_current_month };
	private static final int[] STATE_TODAY = { R.attr.state_today };

	private static final int[] STATE_JOUR_A_AFFICHER = { R.attr.state_jour_a_afficher };

	private static final int[] STATE_HIGHLIGHTED = { R.attr.state_highlighted };
	private static final int[] STATE_RANGE_FIRST = { R.attr.state_range_first };
	private static final int[] STATE_RANGE_MIDDLE = { R.attr.state_range_middle };
	private static final int[] STATE_RANGE_LAST = { R.attr.state_range_last };

	public boolean isSelectable = false;
	public boolean isCurrentMonth = false;
	public boolean isToday = false;
	public boolean isHighlighted = false;
	public boolean isJourAAfficher = false;
	private RangeState rangeState = RangeState.NONE;

	public CalendarCellView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
		refreshDrawableState();
	}

	public void setCurrentMonth(boolean isCurrentMonth) {
		this.isCurrentMonth = isCurrentMonth;
		refreshDrawableState();
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
		refreshDrawableState();
	}

	public void setRangeState(MonthCellDescriptor.RangeState rangeState) {
		this.rangeState = rangeState;
		refreshDrawableState();
	}

	public void setHighlighted(boolean highlighted) {
		isHighlighted = highlighted;
	}

	public void setJourAAfficher(boolean isJourAAfficher) {
		this.isJourAAfficher = isJourAAfficher;
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 5);

		if (isJourAAfficher) {
			mergeDrawableStates(drawableState, STATE_JOUR_A_AFFICHER);
		}

		if (isSelectable) {
			mergeDrawableStates(drawableState, STATE_SELECTABLE);
		}

		if (isCurrentMonth) {
			mergeDrawableStates(drawableState, STATE_CURRENT_MONTH);
		}

		if (isToday) {
			mergeDrawableStates(drawableState, STATE_TODAY);
		}

		if (isHighlighted) {
			mergeDrawableStates(drawableState, STATE_HIGHLIGHTED);
		}

		if (rangeState == MonthCellDescriptor.RangeState.FIRST) {
			mergeDrawableStates(drawableState, STATE_RANGE_FIRST);
		} else if (rangeState == MonthCellDescriptor.RangeState.MIDDLE) {
			mergeDrawableStates(drawableState, STATE_RANGE_MIDDLE);
		} else if (rangeState == RangeState.LAST) {
			mergeDrawableStates(drawableState, STATE_RANGE_LAST);
		}

		return drawableState;
	}

}
