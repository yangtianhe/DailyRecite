package cn.geoff.dailyrecite;

import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPageFragment extends Fragment implements OnClickListener{
	/*
	 * preferences:
	 * 	storedText,storedMeaning,lastInput
	 */
	private SharedPreferences storedData;
	private SharedPreferences.Editor storedDataEditor;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase wordsBookDB, dictionaryDB;
	private EditText wordTextEdit, wordMeaningEdit;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.add_page_fragment, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// 初始化按钮与文本框
		((Button) getActivity().findViewById(R.id.add_word_button)).setOnClickListener(this);
		((Button) getActivity().findViewById(R.id.clear_screen_button)).setOnClickListener(this);
		((Button) getActivity().findViewById(R.id.undo_button)).setOnClickListener(this);
		
		// 获取数据库
		dbHelper = new MyDatabaseHelper(getActivity(), "TestSQL.db", null, 1);
		wordsBookDB = dbHelper.getWritableDatabase();
		DictionaryDatabaseManager dictionaryDatabaseManager = new DictionaryDatabaseManager(getActivity());
	    dictionaryDB = dictionaryDatabaseManager.openDatabase(); 
	    
	    // 初始化文本框
	    storedData = getActivity().getSharedPreferences("storedData", 0);
	    storedDataEditor = storedData.edit();
	    wordTextEdit = (EditText)getActivity().findViewById(R.id.word_text);
		wordMeaningEdit = (EditText)getActivity().findViewById(R.id.word_meaning);
		wordTextEdit.setText(storedData.getString("storedText", ""));
		wordMeaningEdit.setText(storedData.getString("storedMeaning", ""));
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.undo_button:
			if(storedData.getString("lastInput", "") != ""){
				Cursor cursor = dictionaryDB.rawQuery("select * from BackupDictionary where word='"
						+ storedData.getString("lastInput", "") + "'", null);
				if(cursor.moveToFirst()){
					wordTextEdit.setText(cursor.getString(cursor.getColumnIndex("word")));
					wordMeaningEdit.setText(cursor.getString(cursor.getColumnIndex("meaning")));
				}
				
				dictionaryDB.execSQL("delete from BackupDictionary where word='"
						+ storedData.getString("lastInput", "") + "';");
				wordsBookDB.delete("WordsBook", "word = ?", new String[] {storedData.getString("lastInput", "")});
				
				Toast.makeText(getActivity(), "biu,撤销啦~~", Toast.LENGTH_SHORT).show();
				
				storedDataEditor.putString("lastInput", "");
				storedDataEditor.commit();
			}else{
				Toast.makeText(getActivity(), "撤销过了呀~~", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.add_word_button:
			if(!wordTextEdit.getText().toString().equals("") && !wordMeaningEdit.getText().toString().equals("")){
			    Cursor cursor =  dictionaryDB.rawQuery( "select * from BackupDictionary " +
			    		"where word like '" + wordTextEdit.getText().toString() + "'", null);
			    if(!cursor.moveToFirst()){
					ContentValues values = new ContentValues();
								
					values.put("word", wordTextEdit.getText().toString());
					wordsBookDB.insert("WordsBook", null, values);
					values.clear();
					
					dictionaryDB.execSQL("insert into BackupDictionary(word,meaning,nextrecite,gap)"
							+ "values('" 
							+ wordTextEdit.getText().toString() + "','"
							+ wordMeaningEdit.getText().toString() + "','"
							+ String.valueOf(System.currentTimeMillis()) + "','"
							+ "300000" + "')"
							+ ";");
					
					Toast.makeText(getActivity(), "加进去了~~", Toast.LENGTH_SHORT).show();
					
					storedDataEditor.putString("lastInput", wordTextEdit.getText().toString());
					storedDataEditor.commit();
			    }else{
			    	Toast.makeText(getActivity(), "已经有相同条目了诶~~", Toast.LENGTH_SHORT).show();
			    }
			}else{
				Toast.makeText(getActivity(), "输错了好像~~", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.clear_screen_button:
			wordTextEdit.setText("");
			wordMeaningEdit.setText("");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if(!wordTextEdit.getText().toString().equals("") || !wordTextEdit.getText().toString().equals("")){
			storedDataEditor.putString("storedText", wordTextEdit.getText().toString());
			storedDataEditor.putString("storedMeaning", wordMeaningEdit.getText().toString());
			storedDataEditor.commit();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		wordsBookDB.close();
		dictionaryDB.close();
	}
}