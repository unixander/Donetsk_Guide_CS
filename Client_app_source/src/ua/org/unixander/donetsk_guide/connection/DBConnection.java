package ua.org.unixander.donetsk_guide.connection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ua.org.unixander.donetsk_guide.entity.Place;
import ua.org.unixander.donetsk_guide.entity.Route;
import ua.org.unixander.donetsk_guide.entity.Station;
import ua.org.unixander.donetsk_guide.settings.LocalDB;

public class DBConnection {
	private NetworkConnection connection = null;
	private LocalDB dbHelper = null;
	private String host;
	private int port;
	private final static String CLOSE_CONNECTION = "CLOSE_CONNECTION",
			GET_ROUTES_LIST = "GETROUTESLIST",
			GET_PLACES_LIST = "GETPLACESLIST",
			GET_PLACES_TYPES = "GETPLACESTYPES",
			GET_PLACE_WITH_ID = "GETPLACEWITHID",
			GET_ROUTES_FROM_STATION_ID = "GETROUTESFROMSTATIONID",
			GET_ROUTE_WITH_ID = "GETROUTEWITHID",
			GET_STATIONS_FROM_PLACE_ID = "GETSTATIONSFROMPLACEID",
			GET_STATIONS_FROM_ROUTE_ID = "GETSTATIONSFROMROUTEID",
			GET_CLOSE_PLACES_LIST = "GETCLOSEPLACESLIST",
			SEARCH_PLACE = "SEARCHPLACE", SEARCH_ROUTES = "SEARCHROUTES",
			CHECK_CONNECTION = "CHECKCONNECTION",
			UNKNOWN_COMMAND = "UNKNOWNCOMMAND",
			SEARCH_ROUTE_POINTS = "SEARCHROUTEPOINTS";

	public DBConnection() {
		connection = new NetworkConnection();
	}
	
	public DBConnection(String host,int port){
		connection=new NetworkConnection(host, port);
	}
	
	public DBConnection(Context context){
		getSettingsFromDB(context);
		connection=new NetworkConnection(this.host,this.port);
	}
	
	public String getError(){
		return connection.getLastError();
	}
	public void getSettingsFromDB(Context context) {
		dbHelper = new LocalDB(context);
		try {
			dbHelper.createDataBase();
			dbHelper.openDataBase();
			String h = dbHelper.getHost();
			int p = Integer.parseInt(dbHelper.getPort());
			dbHelper.close();
			if(!h.isEmpty()) host=h;
			if(p!=0) port=p;
		} catch (IOException e) {

		}
	}
	public boolean open() {
		return connection.connect();
	}

	public void close() {
		connection.disconnect();
	}

	public void reset(){
		connection.disconnect();
		connection.connect();
	}
	public boolean testDBConnection() {
		String answer = "";
		try {
			answer = connection.sendMessage(CHECK_CONNECTION);
		} catch (Exception e) {

		}
		return answer.equals("CONNECTIONESTABLISHED");
	}
	
	/**
	 * Get full list of routes
	 */
	public List<Route> getRoutesList() {
		List<Route> list = null;
		String answer = connection.sendMessage(GET_ROUTES_LIST);
		Gson gson = new Gson();
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Route>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * Get routes, related to the specified station
	 */
	public List<Route> getRoutesFromStationId(int stationid) {
		List<Route> list = null;
		Gson gson = new Gson();
		String[] request = { GET_ROUTES_FROM_STATION_ID,
				Integer.toString(stationid) };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Route>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * Search route according to it's number, part of a number or/and route type
	 */
	public List<Route> searchRoute(String s, String t) {
		List<Route> list = null;
		Gson gson = new Gson();
		String[] request = { SEARCH_ROUTES, s, t };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Route>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * Get route with the specified ID
	 */
	public Route getRouteWithId(int id) {
		Route route = null;
		Gson gson = new Gson();
		String[] request = { GET_ROUTE_WITH_ID, Integer.toString(id) };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer == null)
			return route;
		if (answer.length() == 0)
			return route;
		route = gson.fromJson(answer, Route.class);
		return route;
	}

	/**
	 * Get full list of places
	 */
	public List<Place> getPlacesList() {
		List<Place> list = null;
		String answer = connection.sendMessage(GET_PLACES_LIST);
		Gson gson = new Gson();
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Place>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * Get list of places, that are situated near the point with specified
	 * coordinates
	 */
	public List<Place> getClosePlacesList(double latitude, double longtitude) {
		List<Place> list = null;
		Gson gson = new Gson();
		String[] request = { GET_CLOSE_PLACES_LIST, Double.toString(latitude),
				Double.toString(longtitude) };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Place>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * Search place according to it's full name/description or it's part, and/or
	 * type of the place
	 */
	public List<Place> searchPlace(String s, String type) {
		List<Place> list = null;
		Gson gson = new Gson();
		String[] request = { SEARCH_PLACE, s, type };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Place>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * Get place with the specified id
	 */
	public Place getPlaceWithId(int id) {
		Place place = null;
		Gson gson = new Gson();
		String[] request = { GET_PLACE_WITH_ID, Integer.toString(id) };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer == null)
			return place;
		if (answer.length() == 0)
			return place;
		place = gson.fromJson(answer, Place.class);
		return place;
	}

	/**
	 * Get list of places types
	 */
	public List<String> getPlacesTypes() {
		List<String> list = null;
		Gson gson = new Gson();
		String answer = connection.sendMessage(GET_PLACES_TYPES);
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		try {
			Type tType = new TypeToken<Collection<String>>() {
			}.getType();
			list = gson.fromJson(answer, tType);
		} catch (Exception e) {

		}
		return list;
	}

	/**
	 * Get list of stations of the specified route
	 */
	public List<Station> getStationsFromRouteId(int id) {
		List<Station> list = null;
		Gson gson = new Gson();
		String[] request = { GET_STATIONS_FROM_ROUTE_ID, Integer.toString(id) };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer == null)
			return list;
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Station>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * Get list of stations from place id
	 */
	public List<Station> getStationsFromPlaceId(int id) {
		List<Station> list = null;
		Gson gson = new Gson();
		String[] request = { GET_STATIONS_FROM_PLACE_ID, Integer.toString(id) };
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer.equals("null"))
			return new ArrayList<Station>();
		if (answer.length() == 0)
			return list;
		Type tType = new TypeToken<Collection<Station>>() {
		}.getType();
		list = gson.fromJson(answer, tType);
		return list;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String>[] searchRouteTwoPoints(double[] one, double[] two,int period) {
		ArrayList<String>[] list = new ArrayList[3];
		Gson gson = new Gson();
		String[] request = { SEARCH_ROUTE_POINTS, Double.toString(one[0]),
				Double.toString(one[1]), Double.toString(two[0]),
				Double.toString(two[1]),Integer.toString(period)};
		String answer = connection.sendMessage(gson.toJson(request));
		if (answer.equals("null") || answer.length() == 0) {
			return null;
		};
		Type tType = new TypeToken<ArrayList<mapPoint>>() {
		}.getType();
		ArrayList<mapPoint> temp=gson.fromJson(answer, tType);
		for(int i=0;i<3;i++){
			list[i]=new ArrayList<String>();
		}
		for(mapPoint p:temp){
			list[0].add(p.name);
			list[1].add(Double.toString(p.latitude));
			list[2].add(Double.toString(p.longtitude));
		}

		return list;
	}
	private class mapPoint{
		String name;
		double latitude;
		double longtitude;
	}
}
