package ua.org.unixander.donetsk_guide.entity;
/**
 * 
 * @author Unixander
 * Structure for storing information about the station in the program
 */
public class Station {
	public int id;
	public String title;
	public String routes;
	private boolean selected=true;
	public double latitude;
	public double longtitude;
	public Station(){
		id=-1;
		title="";
		routes=null;
		selected=false;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isSelected() {
		return selected;
	}
}
