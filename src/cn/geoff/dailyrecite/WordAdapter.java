package cn.geoff.dailyrecite;

import java.util.List;

import cn.geoff.dailyrecite.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WordAdapter extends ArrayAdapter<SingleWord> {
	private int resourceId;
	public WordAdapter(Context context, int textViewResourceId,
			List<SingleWord> objects){
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		SingleWord singleWord = getItem(position);
		
		View view;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.wordText = (TextView) view.findViewById(R.id.word_text);
			viewHolder.wordMeaning = (TextView) view.findViewById(R.id.word_meaning);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.wordText.setText(singleWord.getWord());
		viewHolder.wordMeaning.setText(singleWord.getMeaning());
				
		return view;
	}
	
	class ViewHolder{
		TextView wordText;
		TextView wordMeaning;
	}
}
