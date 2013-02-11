package ua.org.unixander.Server.dbOperations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import ua.org.unixander.Server.entity.Station;
import ua.org.unixander.logger.ConsoleLog;

/**
 * 
 * Methods for route search
 * @author unixander
 *
 */
public class SearchRoute {
	public double x1, y1, x2, y2;
	SQLiteConnection db;
	ConsoleLog console = new ConsoleLog();
	dit2flsModule dit2fls;
	ArrayList<Long[]> routes;
	ArrayList<Long> single;
	public int period;

	public SearchRoute(SQLiteConnection db) {
		this.db = db;
		routes = new ArrayList<Long[]>();
	}

	public List<Station> search() {
		List<Station> list = null;
		double radius = 0.001;
		long firstStation = -1, secondStation = -1, commonRoute;
		List<Long> first, second, temp = null, tempStation = null, tempStationTwo = null;
		for (int i = 0; i < 5; i++) {
			if (firstStation == -1)
				firstStation = getCloseStation(x1, y1, radius);
			if (secondStation == -1)
				secondStation = getCloseStation(x2, y2, radius);
			if (firstStation != -1 && secondStation != -1)
				break;
			else
				radius *= 2;
		}
		first = getRouteId(firstStation);
		second = getRouteId(secondStation);
		commonRoute = getCommonRoute(first, second);
		single = new ArrayList<Long>();
		while (commonRoute > -1) {
			if (!checkInList(new Long[] { commonRoute })) {
				routes.add(new Long[] { commonRoute });
				single.add(commonRoute);
				first.remove(commonRoute);
				second.remove(commonRoute);
			}
			commonRoute = getCommonRoute(first, second);
		}

		Long routeTwo = null;
		for (Long id : first) {
			temp = getStationsIDsOfRoute(id, temp);
			for (Long station : temp) {
				tempStation = getRouteId(station);
				for (Long routeOne : tempStation) {
					if (second.indexOf(routeOne) > -1
							&& single.indexOf(routeOne) == -1) {
						tempStationTwo = new ArrayList<Long>(first);
						routeTwo = getCommonRoute(tempStationTwo,
								tempStation);
						while (routeTwo != -1) {
							if (first.indexOf(routeTwo) > -1
									&& single.indexOf(routeTwo) == -1
									&& routeOne != routeTwo
									&& !checkInList(new Long[] { routeOne,
											routeTwo })) {
								routes.add(new Long[] { routeOne, routeTwo });
							}
							tempStationTwo.remove(routeTwo);
							routeTwo = getCommonRoute(tempStationTwo,
									tempStation);
						}
					}
				}
			}
		}

		Long[] result = getResultRoutes();
		if (result == null) {
			return list;
		}
		for (int i = 0; i < result.length; i++) {
			list = getStationsOfRoute(result[i], list);
		}
		return list;
	}

	public Long[] getResultRoutes() {
		Long[] result = null;
		List<Double> routesWeights = new ArrayList<Double>();
		double minWeight = -1;
		if (routes.size() == 0) {
			return null;
		} else if (period == -1) {
			result = routes.get(0);
		} else {
			Double weight = -1.0;
			for (int i = 0; i < routes.size(); i++) {
				weight = checkRoute(routes.get(i));
				if(weight==null) 
					weight=1.0;
				routesWeights.add(weight);
			}
			if (routesWeights.size() > 0) {
				minWeight = Collections.min(routesWeights);
				result = routes.get(routesWeights.indexOf(minWeight));
			}
		}
		return result;
	}

	public Double checkRoute(Long... routes) {
		double total = 0;
		String path = "";
		Long id;
		Double RoadLoad = 0.0, HumanLoad = 0.0,TotalLoad=0.0,TL=0.0;
		double[] RL=new double[routes.length],HL=new double[routes.length];
		try {
			path = new File(".").getCanonicalPath();
		} catch (IOException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		}
		dit2fls = new dit2flsModule(path);
		if (dit2fls != null) {
			for (int i=0;i<routes.length;i++) {
				id=routes[i];
				TotalLoad=0.0;
				RoadLoad = getRoadLoad(id, period);
				HumanLoad = getHumanLoad(id, period);
				
				if(RoadLoad>-1&&HumanLoad>-1){
					//TotalLoad= dit2fls.toCalculate(RoadLoad, HumanLoad);
					RL[i]=RoadLoad;
					HL[i]=HumanLoad;
				} else {
					RL[i]=1.0;
					HL[i]=1.0;
				}
				total+=TotalLoad;
			}
			TL=dit2fls.toCalculate(RL,HL);
		}
		return TL;
	}

	public boolean checkInList(Long[] r) {
		if (routes == null || routes.size() == 0)
			return false;
		for (int i = 0; i < routes.size(); i++) {
			if (routes.get(i).length != r.length) {
				for (int j = 0; j < r.length; j++) {
					if (single.indexOf(r[j]) > -1)
						return true;
				}
			} else if (Arrays.deepEquals(routes.get(i), r)) {
				return true;
			}
		}
		return false;
	}

	public List<Long> getStationsIDsOfRoute(long id, List<Long> list) {
		if (list == null)
			list = new ArrayList<Long>();
		String select = "SELECT StationList._id "
				+ " FROM StationList , StationRoute , CoordinatesList "
				+ "WHERE StationList.coordinatesid = CoordinatesList._id AND (StationRoute.routeid = ? AND "
				+ "StationRoute.stationid = StationList._id)";
		SQLiteStatement st = null;
		long idT;
		try {
			st = db.prepare(select);
			st.bind(1, id);
			while (st.step()) {
				idT = st.columnLong(0);
				list.add(idT);
			}
		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			st.dispose();
		}
		return list;
	}

	public String getRouteNumber(long id) {
		String select = "select RouteList.number, TransportType.name from "
				+ "TransportType, RouteList where RouteList.typeid=TransportType._id "
				+ "and RouteList._id=?";
		SQLiteStatement st = null;
		String title = "";
		try {
			st = db.prepare(select);
			st.bind(1, id);
			if (st.step() && !st.columnNull(0) && !st.columnNull(1)) {
				title = st.columnString(1) + ":" + st.columnString(0);
			}
		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			st.dispose();
		}
		return title;
	}

	public List<Station> getStationsOfRoute(long id, List<Station> list) {
		if (list == null)
			list = new ArrayList<Station>();
		String title = "\r\n" + getRouteNumber(id);
		String select = "SELECT StationList._id, StationList.name, CoordinatesList.latitude,"
				+ "CoordinatesList.longtitude FROM StationList , StationRoute , CoordinatesList "
				+ "WHERE StationList.coordinatesid = CoordinatesList._id AND (StationRoute.routeid = ? AND "
				+ "StationRoute.stationid = StationList._id) ORDER BY StationList.name";
		SQLiteStatement st = null;
		Station station = null;
		try {
			st = db.prepare(select);
			st.bind(1, id);
			while (st.step()) {
				station = new Station();
				station.id = st.columnInt(0);
				station.title = "Station:" + st.columnString(1) + title;
				station.latitude = st.columnDouble(2);
				station.longtitude = st.columnDouble(3);
				if (list.indexOf(station) == -1)
					list.add(station);
			}
		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			st.dispose();
		}

		return list;
	}

	public long getCommonRoute(List<Long> first, List<Long> second) {
		for (Long i : first) {
			if (second.indexOf(i) > -1) {
				return i;
			}
		}
		return -1;
	}

	public List<Long> getRouteId(long stationId) {
		List<Long> list = new ArrayList<Long>();
		String select = "SELECT StationRoute.routeid FROM StationRoute WHERE StationRoute.stationid=?";
		SQLiteStatement st = null;
		long id;
		try {
			st = db.prepare(select);
			st.bind(1, stationId);
			while (st.step()) {
				id = st.columnLong(0);
				list.add(id);
			}
		} catch (SQLiteException e) {

		} finally {
			st.dispose();
		}

		return list;
	}

	public int getCloseStation(double x1, double y1, double radius) {
		int station = -1;
		String select = "SELECT StationList._id from StationList,CoordinatesList, StationRoute"
				+ " WHERE StationList.coordinatesid = CoordinatesList._id AND "
				+ "StationList._id = StationRoute.stationid AND "
				+ "(ABS(CoordinatesList.latitude-?)*ABS(CoordinatesList.latitude-?)+"
				+ " ABS(CoordinatesList.longtitude-?)*ABS(CoordinatesList.longtitude-?))"
				+ "<?";
		SQLiteStatement st = null;
		try {
			st = db.prepare(select);
			st.bind(1, y1);
			st.bind(2, y1);
			st.bind(3, x1);
			st.bind(4, x1);
			st.bind(5, radius * radius);
			if (st.step() && !st.columnNull(0))
				station = st.columnInt(0);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		} finally {
			st.dispose();
		}
		return station;
	}

	/**
	 * Get road human load from route id and period of day
	 * 
	 * @param id
	 * @param period
	 * @return
	 */
	public double getHumanLoad(long id, int period) {
		double result = -1;
		String select = "SELECT first, second, third, fourth, fifth, _id from HumanLoad where routeid=?";
		SQLiteStatement st = null;
		try {
			st = db.prepare(select);
			st.bind(1, Long.toString(id));
			if (st.step() && !st.columnNull(period - 1)) {
				result = st.columnDouble(period - 1);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}

		return result;
	}

	/**
	 * Get road road load from route id and period of day
	 * 
	 * @param id
	 * @param period
	 * @return
	 */
	public double getRoadLoad(long id, int period) {
		double result = -1;
		String select = "SELECT first, second, third, fourth, fifth, _id from RoadLoad where routeid=?";
		SQLiteStatement st = null;
		try {
			st = db.prepare(select);
			st.bind(1, Long.toString(id));
			if (st.step() && !st.columnNull(period - 1)) {
				result = st.columnDouble(period - 1);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}

		return result;
	}
}
