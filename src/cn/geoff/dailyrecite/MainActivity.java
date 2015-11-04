package cn.geoff.dailyrecite;

import java.util.ArrayList;

import cn.geoff.dailyrecite.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends FragmentActivity{
	
	private ViewPager viewPager;  
    private ArrayList<Fragment> fragmentList;   
    private TextView searchPageText, addPageText, recitePageView;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		// 设置初始碎片内容
        searchPageText = (TextView)findViewById(R.id.search_page_text);  
        addPageText = (TextView)findViewById(R.id.add_page_text);  
        recitePageView = (TextView)findViewById(R.id.recite_page_text);  
        searchPageText.setOnClickListener(new textViewListener(0));  
        addPageText.setOnClickListener(new textViewListener(1));  
        recitePageView.setOnClickListener(new textViewListener(2));
        
        // 设置滑动
        viewPager = (ViewPager)findViewById(R.id.view_pager);  
        fragmentList = new ArrayList<Fragment>();  
        Fragment searchPageFragment= new SearchPageFragment();  
        Fragment addPageFragment = new AddPageFragment();
        Fragment recitePageFragment = new RecitePageFragment();
        fragmentList.add(searchPageFragment);  
        fragmentList.add(addPageFragment);  
        fragmentList.add(recitePageFragment);  
          
        //给ViewPager设置适配器  
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));   
        viewPager.setCurrentItem(0);//设置当前显示标签页为第一页   
	}
	
    public class textViewListener implements View.OnClickListener{  
        private int index=0;  
          
        public textViewListener(int i) {  
            index =i;  
        }  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub  
            viewPager.setCurrentItem(index);  
        }  
    } 

	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
