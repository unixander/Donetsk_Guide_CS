package ua.org.unixander.Server.dbOperations;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;


import ua.org.unixander.Server.entity.Route;
import ua.org.unixander.logger.ConsoleLog;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.Gson;
/**
 * 
 * Database operations with routes
 * @author unixander
 *
 */
public class Routes {
	private static ConsoleLog console=new ConsoleLog();
	
	public static String getRoutesList(SQLiteConnection db) {
		String sql = "SELECT RouteList._id, RouteList.typeid, RouteList.number,"
				+ "RouteList.cost,RouteList.interval,RouteList.time_begin,RouteList.time_end,"
				+ "TransportType.name FROM RouteList ,TransportType WHERE TransportType._id = RouteList.typeid ORDER BY RouteList.typeid, RouteList.number";

		SQLiteStatement st = null;
		Route route = null;
		Collection<Route> collection = new ArrayList<Route>();
		Gson gson = null;
		String answer = null;
		try {
			st = db.prepare(sql);
			while (st.step()) {
				route = new Route();
				route.id = st.columnInt(0);
				route.typeid = st.columnInt(1);
				route.number = st.columnString(2);
				route.cost = st.columnDouble(3);
				route.interval = st.columnDouble(4);
				route.time_begin = st.columnString(5);
				route.time_end = st.columnString(6);
				route.type = st.columnString(7);
				collection.add(route);
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
	
	public static String getRoutesFromStationId(SQLiteConnection db,int stationid) {
		String select = "SELECT RouteList.number, TransportType.name from RouteList, TransportType, StationRoute "
				+ "where RouteList._id=StationRoute.routeid and TransportType._id=RouteList.typeid and StationRoute.stationid=? "
				+ " order by TransportType.name, RouteList.number";
		SQLiteStatement st = null;
		Route route = null;
		Collection<Route> collection = new ArrayList<Route>();
		Gson gson = null;
		String answer = null;

		try {
			st = db.prepare(select);
			st.bind(1, stationid);
			while (st.step()) {
				route = new Route();
				route.number = st.columnString(0);
				route.type = st.columnString(1);
				collection.add(route);
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
	public static String searchRoutes(SQLiteConnection db,String s, String t) {
		String select = "SELECT RouteList._id, RouteList.typeid, RouteList.number,"
				+ "RouteList.cost,RouteList.interval,RouteList.time_begin,RouteList.time_end,"
				+ "TransportType.name FROM RouteList ,TransportType WHERE TransportType._id = RouteList.typeid AND"
				+ " (RouteList.number LIKE ? AND TransportType.name like ?) ORDER BY RouteList.typeid, RouteList.number";
		SQLiteStatement st = null;
		Route route = null;
		Collection<Route> collection = new ArrayList<Route>();
		Gson gson = null;
		String answer = null;
		s="%"+s+"%";
		t="%"+t+"%";
		try {
			st = db.prepare(select);
			st.bind(1, s);
			st.bind(2, t);
			while (st.step()) {
				route = new Route();
				route.id = st.columnInt(0);
				route.typeid = st.columnInt(1);
				route.number = st.columnString(2);
				route.cost = st.columnDouble(3);
				route.interval = st.columnDouble(4);
				route.time_begin = st.columnString(5);
				route.time_end = st.columnString(6);
				route.type = st.columnString(7);
				collection.add(route);
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
	
	public static String getRouteWithId(SQLiteConnection db,int id) {
		String select = "SELECT RouteList._id, RouteList.typeid, RouteList.number,"
				+ "RouteList.cost,RouteList.interval,RouteList.time_begin,RouteList.time_end,"
				+ "TransportType.name FROM RouteList ,TransportType WHERE TransportType._id = RouteList.typeid AND RouteList._id=?";

		SQLiteStatement st = null;
		Route route = null;
		Gson gson = null;
		String answer = null;
		try {
			st = db.prepare(select);
			st.bind(1, id);
			st.step();
				route = new Route();
				route.id = st.columnInt(0);
				route.typeid = st.columnInt(1);
				route.number = st.columnString(2);
				route.cost = st.columnDouble(3);
				route.interval = st.columnDouble(4);
				route.time_begin = st.columnString(5);
				route.time_end = st.columnString(6);
				route.type = st.columnString(7);
			if (route != null) {
				gson = new Gson();
				answer = gson.toJson(route);
			}
		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			st.dispose();
		}
		return answer;
	}
}
