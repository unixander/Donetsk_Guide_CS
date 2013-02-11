package ua.org.unixander.Server.entity;
//Class defines station
public class Station {
	public int id;
	public String title;
	public String routes;
	private boolean selected;
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
