package com.example.nemus.newspaper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
   
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static Fav fav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fav = new Fav();



        /*
      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                      .setAction("Action", null).show();
          }
      });
      */
      
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    
  

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return News.newInstance();
                case 1:
                    return fav;
                case 2:
                    return Rec.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "News";
                case 1:
                    return "Favorite";
                case 2:
                    return "Recent";
            }
            return null;
        }
    }

    public static class News extends Fragment {

        ListView screen = null;

        ArrayAdapter<String> adapter;
        //GetGuardianNews gd = new GetGuardianNews();

        public News() {
        }

        public static News newInstance() {
            News fragment = new News();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_news, container, false);
            final DBConnect dbConnect = new DBConnect(getActivity(), "news.db",null,1);
            screen = (ListView) rootView.findViewById(R.id.news_listView);
            ArrayList<String> saveWord = new ArrayList<String>();

            JSONArray newsArray =null;
            try {
                 newsArray = new GetGuardianNews().execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(newsArray!=null){
                for(int i=0;i<newsArray.length();i++){
                    try {
                        saveWord.add(newsArray.getJSONObject(i).getString("webTitle"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                saveWord.add("Fail News read");
            }

            adapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,saveWord);
            screen.setAdapter(adapter);

            final JSONArray urlCatch = newsArray;

            screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast toast = null;
                    try {
                        //Intent i = new Intent(Intent.ACTION_VIEW);
                        //Uri u = Uri.parse(urlCatch.getJSONObject(position).getString("webUrl"));
                        //i.setData(u);
                        //startActivity(i);
                        toast = Toast.makeText(getActivity(),urlCatch.getJSONObject(position).getString("webUrl"), Toast.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    int lastNum = dbConnect.getLastPos(DBConnect.rec);
                    try {
                        dbConnect.input(DBConnect.rec, urlCatch.getJSONObject(position).getString("webTitle"),urlCatch.getJSONObject(position).getString("webUrl"),lastNum+1);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            });

            screen.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    PopupMenu pop = new PopupMenu(parent.getContext(), view);
                    pop.getMenuInflater().inflate(R.menu.fav_menu_pop,pop.getMenu());

                    final int index = position;
                    //팝업메뉴 리스너 설정
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.favorite){
                                int lastNum = dbConnect.getLastPos(DBConnect.fav);
                                try {
                                    dbConnect.input(DBConnect.fav, urlCatch.getJSONObject(index).getString("webTitle"), urlCatch.getJSONObject(index).getString("webUrl"), lastNum + 1);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                fav.onDetach();

                            }
                            return false;
                        }
                    });
                    pop.show();
                    return false;
                }
            });

            return rootView;
        }
    }

    public static class Fav extends Fragment {

        ListView screen = null;
        static ArrayAdapter<String> adapter;

        public Fav(){}

        public static Fav newInstance(){
            Fav fragment = new Fav();
            return fragment;
        }
        /*
        public static void refrash(Activity a){
            DBConnect dbConnect = new DBConnect(a, "news.db",null,1);
            JSONArray ja = dbConnect.getAll(DBConnect.fav);
            adapter.add();
            adapter.notifyDataSetChanged();
        }
        */
        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fav, container, false);
            final DBConnect dbConnect = new DBConnect(getActivity(), "news.db",null,1);
            screen = (ListView) rootView.findViewById(R.id.fav_listView);
            ArrayList<String> saveWord = new ArrayList<String>();
            final JSONArray ja = dbConnect.getAll(DBConnect.fav);

            try{
                for (int i = 0; i < ja.length(); i++) {
                    try {
                        saveWord.add(ja.getJSONObject(i).getString("webTitle"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }catch(NullPointerException e){
                saveWord.add("No favorite article");
            }

            adapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,saveWord);
            screen.setAdapter(adapter);
            screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = null;
                    try {
                        u = Uri.parse(ja.getJSONObject(position).getString("webUrl"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    i.setData(u);
                    startActivity(i);
                }
            });

            screen.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    PopupMenu pop = new PopupMenu(parent.getContext(), view);
                    pop.getMenuInflater().inflate(R.menu.del_menu_pop,pop.getMenu());

                    final int index = position;
                    //팝업메뉴 리스너 설정
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.delete){
                                String juda = adapter.getItem(index);
                                adapter.remove(juda);
                                dbConnect.remove(DBConnect.fav,index);
                                adapter.notifyDataSetChanged();
                            }
                            return false;
                        }
                    });
                    pop.show();
                    return false;
                }
            });
            return rootView;
        }



    }

    public static class Rec extends Fragment {

        ListView screen = null;
        ArrayAdapter<String> adapter;

        public Rec(){}

        public static Rec newInstance(){
            Rec fragment = new Rec();
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_rec, container, false);
            final DBConnect dbConnect = new DBConnect(getActivity(), "news.db",null,1);
            screen = (ListView) rootView.findViewById(R.id.rec_listView);
            ArrayList<String> saveWord = new ArrayList<String>();
            final JSONArray ja = dbConnect.getAll(DBConnect.rec);

            try{
                for (int i = 0; i < ja.length(); i++) {
                    try {
                        saveWord.add(ja.getJSONObject(i).getString("webTitle"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (i > 10) break;
                }
            }catch(NullPointerException e){
                saveWord.add("No recent article");
            }
            adapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,saveWord);
            screen.setAdapter(adapter);
            screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = null;
                    try {
                        u = Uri.parse(ja.getJSONObject(position).getString("webUrl"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    i.setData(u);
                    startActivity(i);
                }
            });
            screen.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    PopupMenu pop = new PopupMenu(parent.getContext(), view);
                    pop.getMenuInflater().inflate(R.menu.del_menu_pop,pop.getMenu());

                    final int index = position;
                    //팝업메뉴 리스너 설정
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.delete){
                                String juda = adapter.getItem(index);
                                adapter.remove(juda);
                                dbConnect.remove(DBConnect.rec,index);
                                adapter.notifyDataSetChanged();
                            }
                            return false;
                        }
                    });
                    pop.show();
                    return false;
                }
            });
            return rootView;
        }
    }
}
