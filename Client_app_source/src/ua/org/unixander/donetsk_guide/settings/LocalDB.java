package ua.org.unixander.donetsk_guide.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDB extends SQLiteOpenHelper {
	private static String DB_PATH = ""; // Path to the database file
	private static String DB_NAME = "DGSettings"; // name of the database file
	private static int DB_VERSION = 1; // version of the database
	private SQLiteDatabase db;
	private final Context context;

	public LocalDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	}

	/**
	 * Delete database of the current application
	 * 
	 * @throws IOException
	 */
	public void deleteDataBase() throws IOException {
		// boolean dbExist=checkDataBase();
		// if(dbExist){
		String filename = DB_PATH + DB_NAME;
		File file = new File(filename);
		if (file.delete()) {
			// dbExist=checkDataBase();

		}
		// }
	}

	/**
	 * Create database if it doesn't exist. In another case, it checks the
	 * version of existing database and decides if it's necessary to replace
	 * existing db with the newer version
	 * 
	 * @throws IOException
	 */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		SQLiteDatabase db_Read = null;
		if (dbExist) {

		} else {
			db_Read = this.getReadableDatabase();
			db_Read.close();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
		dbExist = checkDataBase();
	}

	/**
	 * Checks if database already exists
	 * 
	 * @return true, if db exists, false, in other cases
	 */
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String Path = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(Path, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database doesn't exist
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copy database file from program to the database folder on Android device
	 * 
	 * @throws IOException
	 */
	private void copyDataBase() throws IOException {
		InputStream input = context.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream output = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		output.flush();
		output.close();
		input.close();
	}

	/**
	 * Open database in read-only mode
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
		String path = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.close();
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	/**
	 * Open database in read-write mode
	 * 
	 * @throws SQLException
	 */
	public void openDataBaseforWrite() throws SQLException {
		String path = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	public String getHost(){
		String answer=null,select="Select Settings.value from Settings where Settings.param=?";
		String[] args={"host"};
		Cursor cursor=db.rawQuery(select,args);
		if(cursor.moveToNext()){
			answer=cursor.getString(0);
		}
		return answer;
	}

	public void updateHost(String host) {
		ContentValues values = new ContentValues();
		String where="param=?";
		String[] args={"host"};
		values.put("param", "host");
		values.put("value", host);
		long id=db.update("Settings", values, where, args);
		if(id==0){
			db.insert("Settings",null,values);
		}
	}
	
	public String getPort(){
		String answer=null,select="Select Settings.value from Settings where Settings.param=?";
		String[] args={"port"};
		Cursor cursor=db.rawQuery(select,args);
		if(cursor.moveToNext()){
			answer=cursor.getString(0);
		}
		return answer;
	}

	public void updatePort(String port) {
		ContentValues values = new ContentValues();
		String where="param=?";
		String[] args={"port"};
		values.put("param", "port");
		values.put("value", port);
		long id= db.update("Settings", values, where, args);
		if(id==0){
			db.insert("Settings", null, values);
		}
	}

	@Override
	public synchronized void close() {
		if (db != null)
			db.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	};

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	};

}
