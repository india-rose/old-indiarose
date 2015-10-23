package org.indiarose.api.fragments.core;

import org.indiarose.api.IndiagramManager;
import org.indiarose.api.activity.IndiaRoseFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;

/**
 * This class have to be extended by all IndiaRose's fragments
 * It provides easy-to-use functions to override fragments's hierarchy
 * @author florentchampigny
 *
 */
public abstract class FragmentNormal extends Fragment implements
OnClickListener {

	boolean fragmentVisible = false;
	LayoutInflater layoutInflater;
	View fragmentView = null; //The fragment (content) view

	/**
	 * This method is called when the fragment is about to be displayed on the screen
	 * @param layoutId The identifier of the layout.xml file which will be inflated to create the fragment 
	 * @return False if the fragment has already been created, True if it is the first appearance (when you will create your views)
	 */
	public boolean onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutId) {

		if (getFragmentView() != null) {
			((ViewGroup) getFragmentView().getParent())
			.removeView(getFragmentView());
			return false;
		} else {
			setLayoutInflater(inflater);
			setFragmentView(inflater.inflate(layoutId, container, false));
			return true;
		}
	}

	/**
	 * Is called when the user press the back button
	 * @return Yes if you catch the input (use the input on the current fragment)
	 */
	public boolean onBackPressed() {
		return false;
	}

	/**
	 * Is calle when the fragment began visible / revisible
	 */
	@Override
	public void onResume() {
		super.onResume();
		try {
			getFragmentView().setOnClickListener(this);
		} catch (Exception e) {
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser == true) {
			Log.d("VISIBLE", "this fragment is now visible");
			fragmentVisible = true;
		}

		else if (isVisibleToUser == false) {
			Log.d("VISIBLE", "this fragment is now invisible");
			fragmentVisible = false;
		}
	}

	/**
	 * Push the fragment on the screen
	 * @param fragment Fragment to be pushed
	 */
	public void ajouterFragment(Object fragment) {
		if (fragment instanceof Fragment)
			ajouterFragment((Fragment) fragment, true);
	}

	/**
	 * Push the fragment on the screen
	 * @param fragment Fragment to be pushed
	 */
	public void ajouterFragment(Fragment fragment) {
		ajouterFragment(fragment, true);
	}

	/**
	 * Push the fragment on the screen
	 * @param fragment Fragment to be pushed
	 * @param Yes if the fragment is the root element of the fragments' stack
	 */
	public void ajouterFragment(Fragment fragment, boolean back) {
		boolean ok = false;
		while(!ok){
			try{
				((IndiaRoseFragmentActivity) getActivity()).ajouterFragment(fragment,back);
				ok = true;
			}catch(Exception e){
			}
		}
	}

	/**
	 * Pop the "i" last fragments
	 * @param i
	 */
	public void retirerFragment(int i) {
		((IndiaRoseFragmentActivity) getActivity()).retirerFragment(i);
	}

	/**
	 * Pop the last fragment
	 */
	public void retirerFragment() {
		((IndiaRoseFragmentActivity) getActivity()).retirerFragment();
	}

	
	@Override
	public void onClick(View v) {

	}

	public boolean isFragmentVisible() {
		return fragmentVisible;
	}

	public void setFragmentVisible(boolean fragmentVisible) {
		this.fragmentVisible = fragmentVisible;
	}

	public View getFragmentView() {
		return fragmentView;
	}

	public void setFragmentView(View fragmentView) {
		this.fragmentView = fragmentView;
	}

	public LayoutInflater getLayoutInflater() {
		return layoutInflater;
	}

	public void setLayoutInflater(LayoutInflater layoutInflater) {
		this.layoutInflater = layoutInflater;
	}

	/**
	 * Override the findViewById to be easily used by a fragment
	 * @param id
	 * @return
	 */
	public View findViewById(int id) {
		return getFragmentView().findViewById(id);
	}

	/**
	 * Return the IndiagramManager
	 * @return the IndiagramManager
	 */
	public IndiagramManager getIndiagramManager() {
		return ((IndiaRoseFragmentActivity) getActivity())
				.getIndiagramManager();
	}

}
