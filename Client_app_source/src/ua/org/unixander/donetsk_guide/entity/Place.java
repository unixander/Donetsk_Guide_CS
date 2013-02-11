package ua.org.unixander.donetsk_guide.entity;
/**
 * 
 * @author Unixander
 * Place structure for storing information about place 
 * in the program
 */
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
