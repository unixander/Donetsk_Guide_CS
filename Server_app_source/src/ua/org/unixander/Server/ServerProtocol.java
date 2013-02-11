package ua.org.unixander.Server;

import ua.org.unixander.logger.ConsoleLog;

import com.almworks.sqlite4java.SQLiteException;
import com.google.gson.Gson;


/**
 * 
 * Defines the language of client-server conversation
 * @author unixander
 *
 */
public class ServerProtocol {
	private static DBAdapter dbAdapter;
	private static ConsoleLog console = new ConsoleLog();
	private final static String CLOSE_CONNECTION = "CLOSE_CONNECTION",
			GET_ROUTES_LIST = "GETROUTESLIST",
			GET_PLACES_LIST = "GETPLACESLIST",
			GET_PLACES_TYPES = "GETPLACESTYPES",
			GET_PLACE_WITH_ID = "GETPLACEWITHID",
			GET_ROUTES_FROM_STATION_ID = "GETROUTESFROMSTATIONID",
			GET_ROUTE_WITH_ID = "GETROUTEWITHID",
			GET_STATIONS_FROM_PLACE_ID = "GETSTATIONSFROMPLACEID",
			GET_STATIONS_FROM_ROUTE_ID = "GETSTATIONSFROMROUTEID",
			SEARCH_PLACE = "SEARCHPLACE", SEARCH_ROUTES = "SEARCHROUTES",
			CHECK_CONNECTION = "CHECKCONNECTION",
			UNKNOWN_COMMAND = "UNKNOWNCOMMAND",
			SEARCH_ROUTE_POINTS = "SEARCHROUTEPOINTS";

	public String processInput(String theInput) {
		String[] command;

		String answer = "WUT?";
		command = theInput.split(" ");
		try {
			dbAdapter = new DBAdapter();
			dbAdapter.close();
			dbAdapter.open();
			if (theInput.equals(CHECK_CONNECTION)) {
				answer = "CONNECTIONESTABLISHED";
			} else if (theInput.equals(CLOSE_CONNECTION)) {
				answer = command[0];
				dbAdapter.close();
			} else if (theInput.equals(GET_ROUTES_LIST)) {
				answer = dbAdapter.getRoutesList().toString();
			} else if (theInput.equals(GET_PLACES_LIST)) {
				answer = dbAdapter.getPlacesList().toString();
			} else if (theInput.equals(GET_PLACES_TYPES)) {
				answer = dbAdapter.getPlacesTypes().toString();
			} else {
				Gson gson = new Gson();
				try {
					command = gson.fromJson(theInput, String[].class);
				} catch (Exception e) {
					return UNKNOWN_COMMAND;
				}
				if (command[0].equals(GET_PLACE_WITH_ID)) {
					answer = dbAdapter.getPlaceWithId(
							Integer.parseInt(command[1])).toString();
				} else if (command[0].equals(GET_ROUTES_FROM_STATION_ID)) {
					answer = dbAdapter.getRoutesFromStationId(
							Integer.parseInt(command[1])).toString();
				} else if (command[0].equals(GET_ROUTE_WITH_ID)) {
					answer = dbAdapter.getRouteWithId(
							Integer.parseInt(command[1])).toString();
				} else if (command[0].equals(GET_STATIONS_FROM_PLACE_ID)) {
					answer = dbAdapter.getStationsFromPlaceId(
							Integer.parseInt(command[1])).toString();
				} else if (command[0].equals(GET_STATIONS_FROM_ROUTE_ID)) {
					answer = dbAdapter.getStationsFromRouteId(
							Integer.parseInt(command[1])).toString();
				} else if (command[0].equals(SEARCH_PLACE)) {
					answer = dbAdapter.searchPlace(command[1], command[2]);
				} else if (command[0].equals(SEARCH_ROUTES)) {
					answer = dbAdapter.searchRoutes(command[1], command[2]);
				} else if (command[0].equals(SEARCH_ROUTE_POINTS)) {
					answer = dbAdapter.searchRoutePoints(
							Double.parseDouble(command[1]),
							Double.parseDouble(command[2]),
							Double.parseDouble(command[3]),
							Double.parseDouble(command[4]),
							Integer.parseInt(command[5]));
				}
			}

		} catch (SQLiteException e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
		} finally {
			dbAdapter.close();
		}
		if(answer==null) answer="null";
		System.out.print(answer);
		return answer;
	}
}
