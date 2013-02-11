package ua.org.unixander.Server.dbOperations;

import java.util.ArrayList;
import java.util.Collection;


import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.Gson;

import ua.org.unixander.Server.entity.Place;
import ua.org.unixander.logger.ConsoleLog;

/**
 * 
 * Database operations with places
 * @author unixander
 *
 */
public class Places {
	private static ConsoleLog console=new ConsoleLog();
	public static String getPlacesList(SQLiteConnection db) {
		String select = "SELECT PlacesList._id, PlacesList.name, PlacesType.name,"
				+ "PlaceDescription.description "
				+ "FROM PlacesList ,PlacesType ,PlaceDescription ,"
				+ "CoordinatesList WHERE PlacesList.typeid=PlacesType._id AND PlacesList.descriptionid = PlaceDescription._id AND "
				+ "PlacesList.coordinatesid = CoordinatesList._id ORDER BY PlacesList.typeid, PlacesList.name";
		SQLiteStatement st = null;
		Place place = null;
		Collection<Place> collection = new ArrayList<Place>();
		Gson gson = null;
		String answer = null;
		try {
			st = db.prepare(select);
			while (st.step()) {
				place = new Place();
				place.id = st.columnInt(0);
				place.name = st.columnString(1);
				place.type = st.columnString(2);
				place.latitude=-1;
				place.longtitude=-1;
				place.description = st.columnString(3);
				collection.add(place);
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
		return answer;
	}
	
	public static String searchPlace(SQLiteConnection db,String s, String type) {
		String select = " SELECT PlacesList._id, PlacesList.name, PlacesType.name, PlaceDescription.description"
				+ " FROM PlacesList ,"
				+ " PlacesType , PlaceDescription, CoordinatesList WHERE PlacesList.coordinatesid = CoordinatesList._id AND "
				+ "PlacesType._id = PlacesList.typeid AND PlacesList.descriptionid = PlaceDescription._id AND "
				+ "(PlacesList.name LIKE ? OR PlaceDescription.description LIKE ?) AND PlacesType.name like ? ORDER BY PlacesList.typeid, PlacesList.name";
		SQLiteStatement st = null;
		Place place = null;
		Collection<Place> collection = new ArrayList<Place>();
		Gson gson = null;
		String answer = null;
		s="%"+s+"%";
		type="%"+type+"%";
		try {
			st = db.prepare(select);
			st.bind(1, s);
			st.bind(2, type);
			while (st.step()) {
				place = new Place();
				place.id = st.columnInt(0);
				place.name = st.columnString(1);
				place.type = st.columnString(2);
				place.description = st.columnString(3);
				collection.add(place);
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
		return answer;
	}
	
	public static String getPlaceWithId(SQLiteConnection db,int id) {
		String select = "SELECT PlacesList._id AS id, PlacesList.name AS name, "
				+ "PlacesType.name AS type,PlaceDescription.description AS description, "
				+ "ImagesList.image AS image,CoordinatesList.latitude AS lat,CoordinatesList.longtitude AS long "
				+ "FROM PlacesList, PlacesType, PlaceDescription, ImagesList, CoordinatesList "
				+ "WHERE PlacesList.typeid = PlacesType._id AND PlacesList.descriptionid = PlaceDescription._id AND "
				+ "(PlacesList.imageid=ImagesList._id OR PlacesList.imageid is NULL) AND (PlacesList.coordinatesid = CoordinatesList._id or"
				+ " PlacesList.coordinatesid is null) AND PlacesList._id=?";
		SQLiteStatement st = null;
		Place place = null;
		Gson gson = null;
		String answer = null;
		try {
			st = db.prepare(select);
			st.bind(1, id);
			st.step();
				place = new Place();
				place.id = st.columnInt(0);
				place.name = st.columnString(1);
				place.type = st.columnString(2);
				place.description = st.columnString(3);
				if (!st.columnNull(4)) {
					place.image = st.columnBlob(4);
				}
				if (!st.columnNull(5)) {
					place.latitude = st.columnDouble(5);
				} else {
					place.latitude=-1;
				}
				if (!st.columnNull(6)) {
					place.longtitude = st.columnDouble(6);
				} else {
					place.longtitude=-1;
				}
			if (place != null) {
				gson = new Gson();
				answer = gson.toJson(place);
			}
		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			st.dispose();
		}
		return answer;
	}
	
	public static String getPlacesTypes(SQLiteConnection db) {
		String select = "SELECT PlacesType.name from PlacesType ORDER BY PlacesType.name";
		SQLiteStatement st = null;
		String type = null;
		Collection<String> collection = new ArrayList<String>();
		Gson gson = null;
		String answer = null;
		try {
			st = db.prepare(select);
			while (st.step()) {
				type = st.columnString(0);
				collection.add(type);
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
		return answer;
	}
}
