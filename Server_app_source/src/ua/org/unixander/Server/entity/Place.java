package ua.org.unixander.Server.entity;
//Class, defines place
public class Place {
	public int id;
	public String name;
	public String type;
	public String description;
	public byte[] image;
	public double latitude;
	public double longtitude;
	public Place(){
		id=-1;
		name=null;
		type=null;
		description=null;
		image=null;
		latitude=Double.NaN;
		longtitude=Double.NaN;
	}
}
