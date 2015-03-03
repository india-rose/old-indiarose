package org.indiarose.frontend.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.R;
import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.utils.ImageManager;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.*;

/**
 * Class to manage the title bar of the application. Slot available : -
 * setTitleInfo(Category) : must be called each time the category change.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class TitleBar {
	protected RelativeLayout m_layout = null;

	protected ImageView m_imageCategoryView = null;
	protected TextView m_textCategoryView = null;
	protected Category m_category = null;

	public TitleBar(RelativeLayout _layout) {
		ImageView logo = new ImageView(_layout.getContext());
		logo.setId(0x0fffff2a);
		logo.setImageResource(R.drawable.logo);
		logo.setAdjustViewBounds(true);
		logo.setMinimumHeight(60);
		logo.setMaxHeight(120);

		m_imageCategoryView = new ImageView(_layout.getContext());
		m_imageCategoryView.setAdjustViewBounds(true);
		m_imageCategoryView.setMinimumHeight(60);
		m_imageCategoryView.setMaxHeight(60);
		m_imageCategoryView.setId(0x0fffff2b);

		m_textCategoryView = new TextView(_layout.getContext());
		m_textCategoryView.setMaxHeight(60);
		m_textCategoryView.setTextColor(Color.BLACK);
		m_textCategoryView.setId(0x0fffff2c);
		m_textCategoryView.setTextSize(15);
		m_textCategoryView.setGravity(Gravity.CENTER_VERTICAL);

		_layout.setBackgroundColor(Color.WHITE);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		_layout.addView(logo, lp);

		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		_layout.addView(m_imageCategoryView, lp);

		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.RIGHT_OF, m_imageCategoryView.getId());
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.setMargins(60, 0, 60, 0);
		_layout.addView(m_textCategoryView, lp);
	}

	public void setTitleInfo(Category _category) {
		if (this.m_category == null || !this.m_category.equals(_category)) {
			try {
				setCategory(_category);
				this.m_category = _category;
			} catch (Exception e) {
				Log.wtf("TitleBar", e);
				try {
					setCategory(this.m_category);
				} catch (Exception e1) {
					Log.wtf("TitleBar", "WTF : ", e1);
				}
			}
		}
	}

	protected void setCategory(Category _category) throws Exception {
		if (_category != null) {
			m_imageCategoryView.setImageBitmap(ImageManager
					.loadImage(PathData.IMAGE_DIRECTORY + _category.imagePath));
			m_textCategoryView.setText(_category.text);
		}
	}
}
