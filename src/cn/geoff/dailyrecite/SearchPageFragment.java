package cn.geoff.dailyrecite;

import cn.geoff.dailyrecite.R;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SearchPageFragment extends Fragment {
	
	private EditText keyWord;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// 按钮监听
		Button searchButton=(Button) getActivity().findViewById(R.id.search_button);
        searchButton.setOnClickListener( new OnClickListener(){
               @Override
               public void onClick(View v){
            	   SearchListActivity.actionStart(getActivity(), "SearchPageFragment", "");
            	   /*
            	   NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            	   Notification notification = new Notification(R.drawable.ic_launcher, "状态栏通知测试", System.currentTimeMillis());
            	   Intent intent = new Intent(getActivity(), ReciteActivity.class);
            	   intent.putExtra("sourceActivity","Notification");
            	   PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);  
            	   notification.setLatestEventInfo(getActivity(), "This is content title", "This is content text", pendingIntent);
       			   manager.notify(1,notification); 
            	   Intent intent = new Intent("cn.geoff.dailyrecite.START_TIMING");
           		   LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
           		   */
              }
        });
        
        // 文本框内容变化监听
        keyWord = (EditText) getActivity().findViewById(R.id.key_word);
        keyWord.addTextChangedListener(keyWordWatcher);
	}
	
	public TextWatcher keyWordWatcher = new TextWatcher() {  
		
		@Override  
		public void onTextChanged(CharSequence s, int start, int before, int count) {    
			// Log.d("KEY_WORD","-1-onTextChanged-->"  + keyWord.getText().toString() + "<--");  
		}  
		  
		@Override  
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
			// Log.d("KEY_WORD","-2-beforeTextChanged-->" + keyWord.getText().toString() + "<--");    
		}  
		  
		@Override  
		public void afterTextChanged(Editable s) {  
			// SearchListActivity.actionStart(getActivity(), "SearchPageFragment", keyWord.getText().toString());
			int lines = keyWord.getLineCount();
			if(lines>1){
				String str = s.toString();
				int cursorStart = keyWord.getSelectionStart();
				int cursorEnd = keyWord.getSelectionEnd();
				if(cursorStart == cursorEnd && cursorStart <str.length() && cursorStart >0){
					str = str.substring(0,cursorStart-1) + str.substring(cursorStart);
				}else{
					str = str.substring(0, s.length()-1);
				}
				keyWord.setText(str);
				keyWord.setSelection(keyWord.getText().length());
			}else if(!keyWord.getText().toString().equals("")){
				SearchListActivity.actionStart(getActivity(), "SearchPageFragment", keyWord.getText().toString());
				keyWord.setText("");
			}
		}  
	};  
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.search_page_fragment, container, false);
		return view;
	}
}
