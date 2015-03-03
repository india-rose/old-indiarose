package org.indiarose.api.adapter;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.R;
import org.indiarose.api.views.IndiagramView;
import org.indiarose.api.views.IndiagramView.IndiagramViewClickDelegate;
import org.indiarose.lib.model.Indiagram;

import android.content.Context;
import android.view.*;
import android.widget.BaseAdapter;

/**
 * The class IndiagramListAdapter can be usefull to display Easily Indiagrams on ListView or GridViews 
 * Please refer to IndiagramViewClickDelegate to obtain informations when an Indiagram is selected
 * @author florentchampigny
 *
 */
public class IndiagramListAdapter extends BaseAdapter {

	private Context context; //Context of the applications
	private List<Indiagram> indiagrams; //List of displayed Indiagrams (and/or Categories)
	private List<View> views = new ArrayList<View>(); //List of generated IndiagramViews

	private static LayoutInflater inflater = null; //The used LayoutInflatter

	private IndiagramViewClickDelegate delegate; //The delegate to be called when an IndiagramView (/ an Indiagram) is selected

	public IndiagramListAdapter(Context context, List<Indiagram> indiagrams,
			IndiagramViewClickDelegate delegate) {
		this.context = context;
		this.indiagrams = indiagrams;
		this.delegate = delegate;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		System.gc();

	}

	@Override
	public int getCount() {
		if (indiagrams != null)
			return indiagrams.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return indiagrams.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		if(position < views.size()){
			view = views.get(position);
		}
		else{
			//Inflate the view from the indiagram_listview_element.xml file
			view = inflater.inflate(R.layout.indiagram_listview_element, null);

			//Create an IndiagramView POJO, witch self-managing the injections of the indiagram on the view, 
			//and users-interractions on itself
			IndiagramView sv = new IndiagramView(indiagrams.get(position), context,
					view, position, delegate);

			//And store the generated view
			views.add(position, view);
		}
		return view;
	}

}
