package com.tagcor.tagcor_personal;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.tagcor.tagcor_personal.adapters.ImageAdapter;
import com.tagcor.tagcor_personal.adapters.MainAdapter;
import com.tagcor.tagcor_personal.beans.MainBean;
import com.tagcor.tagcor_personal.beans.UserBean;
import com.tagcor.tagcor_personal.utils.Config;
import com.tagcor.tagcor_personal.utils.ServiceHandler;

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

/**
 * Created by Navanath on 6/23/2017.
 */

public class LoginActivity extends Activity{

    Button login, signup;
    EditText email, password;

    String user_id;
    private static final String TAG_PID = "user_id";
    GridView subcatlist;
    List<UserBean> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide title bar code here---
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_activity);

        Intent i = getIntent();
        user_id = i.getStringExtra(TAG_PID);
        productList = new ArrayList<>();
        subcatlist = (GridView) findViewById(R.id.subcatlist);
        new GetFriendImageList().execute(user_id);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);

        Intent intent = getIntent();
        user_id = intent.getStringExtra(TAG_PID);

        Button homeActivity = (Button) findViewById(R.id.signup);
        homeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().trim().length()!=0){
                    if(password.getText().toString().trim().length()!=0){

                        UserBean ub = new UserBean();
                        ub.setEmail(email.getText().toString().trim());
                        ub.setPassword(password.getText().toString().trim());

                        new SendLoginDetails().execute(ub);

                    }else {
                        password.setError("Password is Mandatory");
                    }
                }else {
                    email.setError("Email is Mandatory");
                }
            }
        });
    }

    public class SendLoginDetails extends AsyncTask<UserBean, Void, String> {
        Dialog dialog;
        Context context;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new Dialog(LoginActivity.this);
            dialog.setContentView(R.layout.custom1);
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setTitle("Loading Please Wait...");
            //dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(UserBean... params) {

            UserBean ub = params[0];
            ServiceHandler sh = new ServiceHandler();

            String response = sh.makeServiceCalls(Config.urlLog + "?user_email=" + ub.getEmail() + "&user_password=" + ub.getPassword());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (s != null) {
                //TextView nameDisp = (TextView) findViewById(R.id.user_id);
                System.out.print(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String status = jsonObject.getString("Status");
                    String user_id = jsonObject.getString("user_id");
                    if (status.equals("Failure")) {
                        Toast.makeText(LoginActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();
                    } else if (status.equals("Success")) {

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        //final String email;
                        //i.putExtra(UserEmail, email);
                        i.putExtra(TAG_PID, user_id);
                        startActivity(i);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //all friend image code start here...
    public class GetFriendImageList extends AsyncTask{

        String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try{
                URL reurl = new URL(Config.urlImgList+"?user_id="+params[0]);
                URLConnection connection = reurl.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String temp = null;
                StringBuilder sb = new StringBuilder();

                while ((temp = br.readLine())!=null){
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
            if(response!=null) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray jsonArr = jsonObj.getJSONArray("sub_cat");
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject element = jsonArr.getJSONObject(i);

                        String user_id = element.getString("user_id");
                        String user_img = element.optString("path");

                        System.out.println(user_id);
                        System.out.println(user_img);

                        UserBean mb = new UserBean();
                        mb.setUser_id(user_id);
                        mb.setFri_img(user_img);

                        productList.add(mb);
                    }
                    ImageAdapter md = new ImageAdapter(LoginActivity.this, productList);
                    subcatlist.setAdapter(md);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
