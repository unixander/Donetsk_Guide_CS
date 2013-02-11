package ua.org.unixander.donetsk_guide.adapter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import ua.org.unixander.donetsk_guide.R;
import ua.org.unixander.donetsk_guide.entity.Station;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
/**
 * 
 * @author Unixander
 * Adapter for listviews, that displays stations list with checkbox for each station
 */
public class StationAdapter extends ArrayAdapter<Station>{
	private final List<Station> list;
	private final Activity context;

	public StationAdapter(Activity context, List<Station> list) {
		super(context, R.layout.station, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		public TextView text;
		public CheckBox checkbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.station, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.label);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Station element = (Station) viewHolder.checkbox
									.getTag();
							element.setSelected(buttonView.isChecked());

						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).title);
		holder.checkbox.setChecked(true);
		//list.get(position).isSelected()
		return view;
	}
}
