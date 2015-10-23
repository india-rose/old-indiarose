package org.indiarose.indiarosetimer;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.api.views.IndiagramView;

import org.indiarose.indiarosetimer.views.CategorieView;

import org.indiarose.indiarosetimer.modele.Categorie;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CategoryListAdapter extends BaseAdapter{

	public Context context;
	private List<Categorie> categories;
	
	private static LayoutInflater inflater = null;
	private List<CategorieView> views = new ArrayList<CategorieView>();

	
	public CategoryListAdapter(Context context,List<Categorie> categories){
		this.context = context;
		this.categories = categories;
		
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}
	@Override
	public int getCount() {

		if(categories != null)
			return categories.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	View view = null;
	view = inflater.inflate(R.layout.categorie_listview_element,null);
	CategorieView sv = new CategorieView(categories.get(position),context,view,position);
	views.add(position,sv);
	return view;
	}

}
