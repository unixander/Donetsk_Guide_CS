package ua.org.unixander.donetsk_guide.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.entity.Place;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlacesListAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final int DESCRIPTION_LENGTH=64;
	private List<Place> places;
	static class ViewHolder {
		public TextView title;
		public TextView txtdescription;
		public ImageView image;
	}
	public PlacesListAdapter(Activity context, String[] names, List<Place> placesList) {
		super(context, R.layout.placelistitem, names);
		int num=0;
		if(placesList!=null){
			num=placesList.size();
			places=placesList;
		} else {
			places=new ArrayList<Place>();
		}
		names=new String[num];
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		Place place=places.get(position);
		ViewHolder holder;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.placelistitem, null,true);
			holder = new ViewHolder();
			holder.title = (TextView) rowView.findViewById(R.id.title);
			holder.image = (ImageView) rowView.findViewById(R.id.icon);
			holder.txtdescription=(TextView) rowView.findViewById(R.id.description);
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		holder.title.setText(place.name);
		String s=place.description;
		if(s.length()>DESCRIPTION_LENGTH)
			s=place.description.substring(0, DESCRIPTION_LENGTH);
		holder.txtdescription.setText(s);
		String type=place.type;
		type=type.toLowerCase(Locale.US);
		holder.image.setImageResource(GetPlaceIcon(type));

		return rowView;
	}
	public static int GetPlaceIcon(String type){
		type=type.toLowerCase(Locale.US);
		if(type.contains("monument"))
			return R.drawable.monument;
		else
		if(type.contains("museum"))
			return R.drawable.museum;
		else
			if(type.contains("church"))
				return R.drawable.church;
			else
				if(type.contains("park"))
					return R.drawable.park;
				else
					if(type.contains("fountains"))
						return R.drawable.fountains;
		if(type.contains("theat"))
			return R.drawable.theater;
		if(type.contains("entertainment"))
			return R.drawable.entertainment;
		if(type.contains("stadium"))
			return R.drawable.stadium;
		if(type.contains("restaurants"))
			return R.drawable.restaurant;
		if(type.contains("hotel"))
			return R.drawable.hotel;
		return R.drawable.other;
	}
}
