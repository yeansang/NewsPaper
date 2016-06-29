package com.example.nemus.newspaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-06-29.
 */
public class Fav extends Fragment {

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
                            int num = Math.abs(index-dbConnect.getLastPos(DBConnect.fav));
                            dbConnect.remove(DBConnect.fav,num);
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
