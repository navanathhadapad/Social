package com.tagcor.tagcor_personal;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tagcor.tagcor_personal.adapters.Friend_list_Adapter;
import com.tagcor.tagcor_personal.beans.UserBean;
import com.tagcor.tagcor_personal.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.view.View.OnClickListener;

/**
 * Created by Navanath on 7/6/2017.
 */

public class ProfileActivity extends Activity {
    String user_id;
    public static final String TAG_PID = "user_id";

    ImageView button1, button2;
    GridView friendlist;
    List<UserBean> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        Intent i = getIntent();
        user_id = i.getStringExtra(TAG_PID);
        new GetProfileDetails().execute(user_id);

        productList = new ArrayList<>();
        friendlist = (GridView) findViewById(R.id.subcatlist);
        new GetFriendList().execute(user_id);

        friendlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String user_id = productList.get(position).getUser_id();
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                intent.putExtra(TAG_PID, user_id);
                startActivity(intent);
            }
        });

        button1 = (ImageView) findViewById(R.id.b1);
        button2 = (ImageView) findViewById(R.id.b2);
       /* button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("button1");
                button1.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.VISIBLE);
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("button2");
                button2.setVisibility(View.INVISIBLE);
                button1.setVisibility(View.VISIBLE);
            }
        });*/
        button1.setOnClickListener(onClickListener);
        button2.setOnClickListener(onClickListener);
    }
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.b1:
                    button1.setVisibility(View.INVISIBLE);
                    button2.setVisibility(View.VISIBLE);
                    break;
                case R.id.b2:
                    button2.setVisibility(View.INVISIBLE);
                    button1.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    public class GetProfileDetails extends AsyncTask{
        String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                URL reurl = new URL(Config.urlProInfo+"?user_id="+params[0]);
                URLConnection connection  = reurl.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String temp = null;
                StringBuilder sb = new StringBuilder();

                while((temp = br.readLine())!=null){
                    sb.append(temp);
                    sb.append("\n");
                }
                response = sb.toString();
            }catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(response!=null){
                ImageView banner = (ImageView) findViewById(R.id.banner);
                ImageView pro_img = (ImageView) findViewById(R.id.pro_img);
                TextView name = (TextView) findViewById(R.id.name);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("sub_cat");

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject element = jsonArray.getJSONObject(i);
                        String user_id1 = element.getString("user_id");
                        String cover = element.optString("path1");
                        String img_pro = element.optString("path");
                        String fname = element.getString("fname");
                        String lname = element.getString("lname");

                        System.out.println(user_id1);
                        Glide.with(ProfileActivity.this).load(cover).into(banner);
                        Glide.with(ProfileActivity.this).load(img_pro).into(pro_img);
                        name.setText(name.getText() + fname +" "+ lname);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class GetFriendList extends AsyncTask{
        String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try{
                URL reurl = new URL(Config.urlFriList + "?user_id=" + params[0]);
                URLConnection urlConnection = reurl.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String temp = null;
                StringBuilder sb = new StringBuilder();
                while ((temp=br.readLine())!=null){
                    sb.append(temp);
                    sb.append("\n");
                }
                response = sb.toString();
            }catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(response != null){
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("sub_cat");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject element = jsonArray.getJSONObject(i);

                        String user_id = element.getString("user_id");
                        String fri_img = element.optString("and_image");
                        String fri_name = element.getString("fname");
                        String fri_lname = element.getString("lname");

                        System.out.println(user_id);
                        System.out.println(fri_img);
                        System.out.println(fri_name);
                        System.out.println(fri_lname);

                        UserBean ub = new UserBean();
                        ub.setUser_id(user_id);
                        ub.setFri_img(fri_img);
                        ub.setFname(fri_name);
                        ub.setLname(fri_lname);

                        productList.add(ub);
                    }

                    Friend_list_Adapter fa = new Friend_list_Adapter(ProfileActivity.this, productList);
                    friendlist.setAdapter(fa);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
