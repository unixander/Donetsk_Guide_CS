package ua.org.unixander.Server.entity;
//Class defines route
public class Route {
	public int id;
	public int typeid;
	public String type;
	public String number;
	public double cost;
	public double interval;
	public String time_begin;
	public String time_end;	
	public Route(){
		id=-1;
		typeid=-1;
		type=new String();
		number=new String();
		cost=-1;
		interval=-1;
		time_begin=new String();
		time_end=new String();
	}
}
