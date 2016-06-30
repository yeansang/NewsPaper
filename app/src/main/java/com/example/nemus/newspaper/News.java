package com.example.nemus.newspaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;



public class News extends Fragment {

    ListView screen = null;

    ArrayAdapter<String> adapter;
    //GetGuardianNews gd = new GetGuardianNews();
    static final String URI = "content://com.example.nemus.newspaper.ConnectContentProvider/news";

    public News() {
    }

    public static News newInstance(Fav fav) {
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
        Log.d("tag", "news create");

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
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(urlCatch.getJSONObject(position).getString("webUrl"));
                    i.setData(u);
                    startActivity(i);
                    toast = Toast.makeText(getActivity(),urlCatch.getJSONObject(position).getString("webUrl"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                int lastNum = dbConnect.getLastPos(DBConnect.rec);

                try {
                    dbConnect.removeOld(DBConnect.rec,urlCatch.getJSONObject(position).getString("webTitle"));
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
                                Log.d("call", lastNum+"");
                                Log.d("call",urlCatch.getJSONObject(index).getString("webUrl"));
                                getActivity().getContentResolver().notifyChange(Uri.parse(URI),null);
                                dbConnect.removeOld(DBConnect.fav,urlCatch.getJSONObject(index).getString("webTitle"));
                                dbConnect.input(DBConnect.fav, urlCatch.getJSONObject(index).getString("webTitle"),urlCatch.getJSONObject(index).getString("webUrl"),lastNum+1);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });
                pop.show();
                return false;
            }
        });
        dbConnect.close();

        return rootView;
    }


}