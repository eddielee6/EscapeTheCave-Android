package me.eddielee.escapethecave.dataaccess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "EscapeTheCave.db";
	
	private AssetManager assetManager;
	
	public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        assetManager = context.getAssets();
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL(GetCreateDatabaseScript());
		executeSQLScript(db, "createDatabase.sql");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			//Shit's out of date
		}
	}
	
	//From: http://www.drdobbs.com/database/using-sqlite-on-android/232900584
	private void executeSQLScript(SQLiteDatabase database, String sqlFileName) {
	   ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	   byte buf[] = new byte[1024];
	   int len;

	   InputStream inputStream = null;

	   try {
	      inputStream = assetManager.open(sqlFileName);
	      while ((len = inputStream.read(buf)) != -1) {
	         outputStream.write(buf, 0, len);
	      }
	      outputStream.close();
	      inputStream.close();

	      String[] createScript = outputStream.toString().split(";");
	      for (int i = 0; i < createScript.length; i++) {
	         String sqlStatement = createScript[i].trim();
	         // TODO You may want to parse out comments here
	         if (sqlStatement.length() > 0) {
	            database.execSQL(sqlStatement + ";");
	         }
	      }
	   } catch (IOException e) {
	   // TODO Handle Script Failed to Load
	   } catch (SQLException e) {
	   // TODO Handle Script Failed to Execute
	   }
	}
}