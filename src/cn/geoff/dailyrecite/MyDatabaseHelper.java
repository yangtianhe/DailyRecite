package cn.geoff.dailyrecite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public static final String CREATE_WORDSBOOK = "create table WordsBook ("
			+ "id integer primary key autoincrement, "
			+ "word text)";
	public static final String CREATE_RECITELIST = "create table ReciteList ("
			+ "id integer primary key autoincrement, "
			+ "word text, "
			+ "meaning text, "
			+ "passed integer)";
	
	private Context mContext;
	
	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_WORDSBOOK);
		db.execSQL(CREATE_RECITELIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
