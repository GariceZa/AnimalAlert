package android.handyapps.gareth.animalalert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by Gareth on 2014-12-17.
 * for info go to the following github project: https://github.com/astuetz/PagerSlidingTabStrip
 */
public class MainActivity extends FragmentActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the sliding page tabs
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        //-------------------------
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(),"You clicked settings",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_signout:
                Preferences prefs = new Preferences();
                prefs.removeSharedPrefs(MainActivity.this);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter{

        private final String[] TITLES = {"Alert Map","Alert List","Create Alert"};


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new MapsFragment();

                case 1:
                    return new AlertsFragment();

                case 2:
                    return new AddAlertFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }
}
