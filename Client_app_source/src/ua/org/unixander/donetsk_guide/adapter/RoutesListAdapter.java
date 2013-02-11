package ua.org.unixander.donetsk_guide.adapter;
import java.util.ArrayList;
import java.util.List;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.entity.Route;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RoutesListAdapter extends ArrayAdapter<String> {
	private Activity context;
	private List<Route> routes;
	
	static class ViewHolder{
		public TextView title;
		public TextView txtdescription;
		public ImageView image;
		public int id;
	}
	
	public RoutesListAdapter(Activity context, String[] names, List<Route> rList) {
		super(context, R.layout.routelistitem, names);
		int num=0;
		if(rList!=null){
			num=rList.size();
			routes=rList;
		} else {
			routes=new ArrayList<Route>();
		}
		names=new String[num];
		
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		Route route=routes.get(position);
		if(route==null) return rowView;
		ViewHolder holder;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.routelistitem, null,true);
			holder = new ViewHolder();
			holder.title = (TextView) rowView.findViewById(R.id.title);
			holder.image = (ImageView) rowView.findViewById(R.id.icon);
			holder.txtdescription=(TextView) rowView.findViewById(R.id.description);
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		String s = route.type;
		
		holder.title.setText("Route number: "+route.number);
		holder.txtdescription.setText("Cost: "+route.cost+" UAH || Interval: "+route.interval+" min.");
		holder.id=route.id;
		
		if (s.startsWith("tram")) {
			holder.image.setImageResource(R.drawable.tram);
		} else if (s.startsWith("trolley")){
			holder.image.setImageResource(R.drawable.trolley);
		} else {
			holder.image.setImageResource(R.drawable.bus);
		}
		return rowView;
	}
	
}
