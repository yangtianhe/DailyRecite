package cn.geoff.dailyrecite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DictionaryDatabaseManager {
	private final int BUFFER_SIZE = 10000; 
	private SQLiteDatabase database;
	private Context context;


	public DictionaryDatabaseManager(Context context){
		this.context = context; 
	}
	 
	public SQLiteDatabase openDatabase(){ 
        File sdFile = Environment.getExternalStorageDirectory();
        File dictionaryPath = new File(sdFile.getPath()+"/dailyrecite/backup.db");
        Log.d("dictionaryPath",dictionaryPath.getPath());
        
        if (!dictionaryPath.exists()){ 
            try{
	            //´´½¨Ä¿Â¼
	            File dailyrecitePath = new File(sdFile.getPath()+"/dailyrecite");
	            dailyrecitePath.mkdirs();
	            
	            AssetManager am = this.context.getAssets(); 
				InputStream is= am.open("backup.mp3");
				FileOutputStream fos = new FileOutputStream(dictionaryPath);
				 
				byte[] buffer = new byte[BUFFER_SIZE];
	            int count = 0;
	            while ((count = is.read(buffer)) > 0) {
	            	fos.write(buffer, 0, count);
	            }
				fos.flush();
			
				fos.close();
				is.close();
				am.close();
			}catch (IOException e){  
				e.printStackTrace();
			} 
        }
        database = SQLiteDatabase.openOrCreateDatabase(dictionaryPath, null); 
        return database;
	}
	 
	public void close(){
		if (database != null){
			this.database.close(); 
		}
	}
}
