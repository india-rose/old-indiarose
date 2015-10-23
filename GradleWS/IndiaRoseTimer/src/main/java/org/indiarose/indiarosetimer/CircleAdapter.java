package org.indiarose.indiarosetimer;

import java.util.ArrayList;

import org.indiarose.indiarosetimer.modele.TimerModele;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CircleAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<TimerModele> timers;

	public CircleAdapter(Context c,ArrayList<TimerModele> timers) {
		mContext = c;
		this.timers = timers;
	}

	public int getCount() {
		return timers.size() + 1;
	}

	public Object getItem(int position) {
		return this.timers.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void setTimers(ArrayList<TimerModele> timers){
		this.timers = timers;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {  
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams( GridView.LayoutParams.WRAP_CONTENT,  200));
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		if(position == 0){
			imageView.setImageResource(R.drawable.chronovide);
		}else{
			
			switch (timers.get(position-1).getTypeChrono()) {
			case 1:
				//TODO changer
				imageView.setImageResource(R.drawable.chrono);
				break;
				
			case 2:
				//TODO changer
				imageView.setImageResource(R.drawable.chronoleft);
				break;
			}
		}
		return imageView;
	}
}
