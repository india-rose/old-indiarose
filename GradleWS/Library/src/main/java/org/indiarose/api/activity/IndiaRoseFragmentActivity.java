package org.indiarose.api.activity;

import java.util.LinkedList;

import org.indiarose.library.R;
import org.indiarose.api.IndiagramManager;
import org.indiarose.api.fragments.core.FragmentNormal;

import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.View;
import android.view.WindowManager;

/**
 * This Activity's subclass should be extended on future IndiaRose's applications
 * This class is responsible for the fragment's organizations, 
 * And create the IndiaGram Manager at it start
 * 
 * Please start your Activity's life circle on the onIndiagramManagerInitialised() method
 * 
 * @author florentchampigny
 *
 */
public abstract class IndiaRoseFragmentActivity extends
FragmentActivity implements View.OnClickListener,
OnBackStackChangedListener, IndiagramManager.IndiagramManagerDelegate {

	static LinkedList<Fragment> fragments = new LinkedList<Fragment>();

	static IndiagramManager indiagramManager;

	boolean destroy = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onResume();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (indiagramManager == null)
			indiagramManager = IndiagramManager.getInstance(this, this);
		else{
			this.onIndiagramManagerInitialised();
		}
	}

	/**
	 * Called method when the indiagram manager is initialised
	 * Call your ajouterVues(), charger() and ajouterListener() in this method
	 */
	@Override
	public abstract void onIndiagramManagerInitialised();

	@Override
	protected void onStop() {
		try {
			super.onStop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create your views or call your "findViewById" on this method
	 */
	protected abstract void ajouterVues();

	/**
	 * Modify your views on this method
	 * For example, call yours textView.setText("") here
	 */
	protected abstract void charger();

	/**
	 * Add yours listeners on this method
	 */
	protected void ajouterListeners() {
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}

	/**
	 * Push the fragment on the screen
	 * @param fragment The fragment to be pushed on front of the screen
	 */
	public void ajouterFragment(Fragment fragment) {
		ajouterFragment(fragment, true);
	}

	/**
	 * Pop "nombre" fragments
	 * @param nombre number of fragments to be popped
	 */
	public void retirerFragment(final int nombre) {
		runOnUiThread(new Runnable() {
			public void run() {

				for (int i = 0; i < nombre; ++i) {
					try {
						FragmentTransaction transaction = getSupportFragmentManager()
								.beginTransaction();

						Fragment fragment = fragments.removeLast();
						try{
							fragment.onPause();
						}catch(Exception e){
							e.printStackTrace();
						}

						transaction.remove(fragment);

						transaction.commitAllowingStateLoss();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//getSupportFragmentManager().executePendingTransactions();
				updateLast();
			}
		});
	}

	protected void updateLast(){
		if (fragments.size() > 0) {
			try {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();

				transaction.replace(R.id.fragment_container, fragments.getLast());

				transaction.addToBackStack(null);

				transaction.commitAllowingStateLoss();

				//getSupportFragmentManager().executePendingTransactions();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Pop the last fragment
	 */
	public void retirerFragment() {
		retirerFragment(1);
	}

	/**
	 * Add a fragment to the front of the screen
	 * @param fragment fragment to be pushed
	 * @param back True if the fragment began the root element
	 */
	public void ajouterFragment(final Fragment fragment, final boolean back) {

		runOnUiThread(new Runnable() {
			public void run() {
				if (!back)
					fragments.clear();

				fragments.addLast(fragment);

				System.out.println(fragments);

				try {
					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();

					transaction.replace(R.id.fragment_container, fragment);

					if (back)
						transaction.addToBackStack(null);

					transaction.commitAllowingStateLoss();

					//getSupportFragmentManager().executePendingTransactions();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	public void onBackStackChanged() {
		FragmentManager manager = getSupportFragmentManager();

		if (manager != null) {
			try {
				Fragment currFrag = (Fragment) manager
						.findFragmentById(R.id.fragment_container);

				currFrag.onResume();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void quitter() {
		destroy = true;
		this.finish();
	}

	@Override
	public void onBackPressed() {
		FragmentManager manager = getSupportFragmentManager();

		if (manager != null) {

			try{
				if(fragments.getLast() != null && ((FragmentNormal)fragments.getLast()).onBackPressed()){
				}
				else{
					retirerFragment();

					if (fragments.size() == 0) {
						quitter();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public static LinkedList<Fragment> getFragments() {
		return fragments;
	}

	public static void setFragments(LinkedList<Fragment> fragments) {
		IndiaRoseFragmentActivity.fragments = fragments;
	}

	@Override
	protected void onPause() {
		try {
			super.onPause();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		try {
			super.onDestroy();
		} catch (Exception e) {
			// e.printStackTrace();
		}

		if (destroy)
			android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * Return the IndiagramManager, with provide API to use Indiarams
	 * @return the IndiagramManager
	 */
	public IndiagramManager getIndiagramManager() {
		return indiagramManager;
	}

	@Override
	public void onClick(View v) {

	}

}