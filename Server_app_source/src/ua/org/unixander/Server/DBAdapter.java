package ua.org.unixander.Server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import ua.org.unixander.Server.dbOperations.Places;
import ua.org.unixander.Server.dbOperations.Routes;
import ua.org.unixander.Server.dbOperations.SearchRoute;
import ua.org.unixander.Server.dbOperations.Stations;
import ua.org.unixander.Server.entity.Station;
import ua.org.unixander.Server.entity.mapPoint;
import ua.org.unixander.logger.ConsoleLog;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.google.gson.Gson;

/**
 * 
 * Class that calls proper methods according to user request
 * @author unixander
 *
 */
public class DBAdapter {
	private static SQLiteConnection db;
	private static String DB_PATH = "\\db\\DG_Database.sqlite";
	private static File dbFile;
	private static ConsoleLog console = new ConsoleLog();

	public static SQLiteConnection getConnection(){
		return db;
	}
	
	public DBAdapter() {
		if (checkDbFileExist()) {
			db = new SQLiteConnection(dbFile);
		}
	}

	private boolean checkDbFileExist() {
		String path = "";
		try {
			path = new File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbFile = new File(path + DB_PATH);
		if (!dbFile.exists()) {
			console.Log("Database file not found", ConsoleLog.ERRORMSG);
			dbFile = null;
			return false;
		}
		return true;
	}

	public void open() throws SQLiteException {
		if (db != null && !db.isOpen())
			db.openReadonly();
	}

	public void close() {
		if (db.isOpen())
			db.dispose();
	}

	public String getRoutesList() {
		return Routes.getRoutesList(db);
	}

	public String getRoutesFromStationId(int stationid) {
		return Routes.getRoutesFromStationId(db, stationid);
	}

	public String searchRoutes(String s, String t) {
		return Routes.searchRoutes(db, s, t);
	}

	public String getPlacesList() {
		return Places.getPlacesList(db);
	}

	/**
	 * Search place according to it's full name/description or it's part, and/or
	 * type of the place
	 */
	public String searchPlace(String s, String type) {
		return Places.searchPlace(db, s, type);
	}

	/**
	 * Get route with the specified ID
	 */
	public String getRouteWithId(int id) {
		return Routes.getRouteWithId(db, id);
	}

	/**
	 * Get place with the specified id
	 */
	public String getPlaceWithId(int id) {
		return Places.getPlaceWithId(db, id);
	}

	/**
	 * Get list of places types
	 */
	public String getPlacesTypes() {
		return Places.getPlacesTypes(db);
	}

	/**
	 * Get list of stations of the specified route
	 */
	public String getStationsFromRouteId(int id) {
		return Stations.getStationsFromRouteId(db, id);
	}

	/**
	 * Get list of stations from place id
	 */
	public String getStationsFromPlaceId(int id) {
		return Stations.getStationsFromPlaceId(db, id);
	}
	
	public String searchRoutePoints(double lat1,double long1,double lat2,double long2,int period){
		String answer=null;
		SearchRoute searchRoute=new SearchRoute(db);
		searchRoute.x1=lat1;
		searchRoute.y1=long1;
		searchRoute.x2=lat2;
		searchRoute.y2=long2;
		searchRoute.period=period;
		Collection<Station> list=searchRoute.search();
		if(list==null) return "null";
		Collection<mapPoint> collection=new ArrayList<mapPoint>();
		mapPoint mp;
		for(Station s: list){
			mp=new mapPoint();
			mp.name=s.title;
			mp.latitude=s.latitude;
			mp.longtitude=s.longtitude;
			collection.add(mp);
		}
		Gson gson=new Gson();
		try{
			if(collection!=null&&!collection.isEmpty()){
				answer=gson.toJson(collection);
			}
		} catch (Exception e){
			console.Log(e.getMessage(),ConsoleLog.ERRORMSG);
		}
		return answer;
	}
}
