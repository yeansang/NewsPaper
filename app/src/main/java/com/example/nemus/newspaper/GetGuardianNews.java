package com.example.nemus.newspaper;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GetGuardianNews extends AsyncTask<Void, Void, JSONArray> {

    final String apiKey = "&api-key=7ef3d39f-2a09-483a-876d-6d9f39720195";
    String url = "http://content.guardianapis.com/search?";
    String msg = "q=debate&order-by=newest";

    public void makeMsg(String msg){
        this.msg = "";
        this.msg += msg;
    }

    public String getNews(){
        try {
            URL address = new URL(url+msg+apiKey);
            HttpURLConnection conn = (HttpURLConnection) address.openConnection();

            conn.setConnectTimeout(10000);

            int rescode = conn.getResponseCode();

            if(rescode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(in,"utf-8"));
                String recive="";
                while((recive = bufreader.readLine())!=null){ }

                return null;
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        try {
            URL address = new URL(url+msg+apiKey);
            HttpURLConnection conn = (HttpURLConnection) address.openConnection();

            conn.setConnectTimeout(10000);

            int rescode = conn.getResponseCode();

            if(rescode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                InputStreamReader inReader = new InputStreamReader(in);
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(in,"utf-8"));
                String recive="";
                String save="";
                while((recive = bufreader.readLine())!=null) {
                    save += recive;
                }

                JSONArray ja = null;

                try {
                    JSONObject js = new JSONObject(save);
                    ja = js.getJSONObject("response").getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return ja;
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}