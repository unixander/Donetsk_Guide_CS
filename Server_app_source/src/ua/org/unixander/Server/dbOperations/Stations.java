package ua.org.unixander.Server.dbOperations;

import java.util.ArrayList;
import java.util.Collection;


import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.Gson;

import ua.org.unixander.Server.entity.Station;
import ua.org.unixander.logger.ConsoleLog;

/**
 * 
 * Operations with stations
 * @author unixander
 *
 */
public class Stations {
	private static ConsoleLog console = new ConsoleLog();
	
	/**
	 * Get list of stations of the specified route
	 */
	public static String getStationsFromRouteId(SQLiteConnection db,int id) {
		String select = "SELECT StationList._id, StationList.name, CoordinatesList.latitude,"
				+ "CoordinatesList.longtitude FROM StationList , StationRoute , CoordinatesList "
				+ "WHERE StationList.coordinatesid = CoordinatesList._id AND (StationRoute.routeid = ? AND "
				+ "StationRoute.stationid = StationList._id) ORDER BY StationList.name";
		SQLiteStatement st = null;
		Station station = null;
		Collection<Station> collection = new ArrayList<Station>();
		Gson gson = null;
		String answer = null;
		try {
			st = db.prepare(select);
			st.bind(1, id);
			while (st.step()) {
				station = new Station();
				station.id = st.columnInt(0);
				station.title = st.columnString(1);
				station.latitude = st.columnDouble(2);
				station.longtitude = st.columnDouble(3);
				collection.add(station);
			}
			if (!collection.isEmpty()) {
				gson = new Gson();
				answer = gson.toJson(collection);
			}
		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			st.dispose();
		}
		if(answer==null) answer="null";
		return answer;
	}

	/**
	 * Get list of stations from place id
	 */
	public static String getStationsFromPlaceId(SQLiteConnection db,int id) {
		String select = "SELECT StationList.name, CoordinatesList.latitude, CoordinatesList.longtitude, StationList._id FROM "
				+ "CoordinatesList , StationPlace , StationList WHERE StationPlace.stationid = StationList._id AND "
				+ "StationPlace.placeid = ? AND StationList.coordinatesid = CoordinatesList._id ORDER BY StationList.name";
		SQLiteStatement st = null;
		Station station = null;
		Collection<Station> collection = new ArrayList<Station>();
		Gson gson = null;
		String answer = null;
		try {
			st = db.prepare(select);
			st.bind(1, id);
			while (st.step()) {
				station = new Station();
				station.id = st.columnInt(3);
				station.title = st.columnString(0);
				station.latitude = st.columnDouble(1);
				station.longtitude = st.columnDouble(2);
				collection.add(station);
			}
			if (!collection.isEmpty()) {
				gson = new Gson();
				answer = gson.toJson(collection);
			}
		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			st.dispose();
		}
		if(answer==null) answer="null";
		return answer;
	}
}
