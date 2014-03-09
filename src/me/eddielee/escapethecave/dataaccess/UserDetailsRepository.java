package me.eddielee.escapethecave.dataaccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDetailsRepository {
	
	private SQLiteDatabase _db;
	
	public UserDetailsRepository(Context context) {
		 _db = new DatabaseHandler(context).getWritableDatabase();
	}
	
	public String GetNameForUser(int userId) {
	    Cursor recordset = _db.rawQuery("SELECT Name FROM UserDetails WHERE UserId = ?", new String[] { String.valueOf(userId) });
	    recordset.moveToFirst();
    	return recordset.getString(0);
	}
	
	public void SetNameForUser(int userId, String newName) {
		Cursor recordset = _db.rawQuery("UPDATE UserDetails SET Name = ? WHERE UserId = ?", new String[] { newName, String.valueOf(userId) });
	    recordset.moveToFirst(); //Doesn't work without this
	    recordset.close();
	}
	
	public int GetSkillLevelForUser(int userId) {
		Cursor recordset = _db.rawQuery("SELECT SkillLevel FROM UserDetails WHERE UserId = ?", new String[] { String.valueOf(userId) });
	    recordset.moveToFirst();
    	return recordset.getInt(0);
	}
	
	public void SetSkillLevelForUser(int userId, int newSkillLevel) {
		Cursor recordset = _db.rawQuery("UPDATE UserDetails SET SkillLevel = ? WHERE UserId = ?", new String[] { String.valueOf(newSkillLevel), String.valueOf(userId) });
	    recordset.moveToFirst(); //Doesn't work without this
	    recordset.close();
	}
}
