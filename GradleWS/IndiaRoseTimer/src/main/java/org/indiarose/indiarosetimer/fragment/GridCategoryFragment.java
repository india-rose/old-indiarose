package org.indiarose.indiarosetimer.fragment;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.indiarosetimer.modele.Categorie;

import org.indiarose.api.fragments.core.FragmentNormal;
import org.indiarose.lib.model.Indiagram;

import org.indiarose.indiarosetimer.CategoryListAdapter;
import org.indiarose.indiarosetimer.R;

import org.indiarose.indiarosetimer.database.base.AccessBaseTimer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class GridCategoryFragment extends FragmentNormal{

	AbsListView list;
	CategoryListAdapter adapter;
	List<Categorie> categorie_base;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		this.onResume();
		if(super.onCreateView(inflater, container, savedInstanceState,
				R.layout.grid_category)){
			
			ajouterVues();
			chargerVues();
			ajouterListener();
		}
		
		return getFragmentView();
	}

	private void ajouterListener() {
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				if(position == 0){
					ajouterFragment(new CreationCategorieFragment(), false);
				}else{
					ajouterFragment(new CategorieFragment(categorie_base.get(position-1).getId(),0),false);
				}
			}
		});
	}

	private void chargerVues() {
		List<Categorie> categories = new ArrayList<Categorie>();
		Indiagram creation = new Indiagram();
		creation.text = "Nouvelle categorie";
		categories.add(new Categorie(creation,0));
		AccessBaseTimer db = new AccessBaseTimer(getActivity());
		db.open();
		this.onResume();
		categorie_base = db.getAllCategory(getIndiagramManager());
		categories.addAll(categorie_base);
		db.close();
		adapter = new CategoryListAdapter(getActivity(),categories);
		
		list.setAdapter(adapter);		
	}

	private void ajouterVues() {
		list = (AbsListView) findViewById(android.R.id.list);		
	}
	
	@Override
	public boolean onBackPressed(){
		super.onBackPressed();
		ajouterFragment(new Home(),false);
		return true;
		
	}
}
