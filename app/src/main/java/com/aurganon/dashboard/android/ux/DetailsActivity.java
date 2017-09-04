package com.aurganon.dashboard.android.ux;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.aurganon.dashboard.android.R;
import com.aurganon.dashboard.android.helpers.AppConfig;
import com.aurganon.dashboard.android.helpers.AppController;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends Activity {
  private static final String TAG = DetailsActivity.class.getSimpleName();

  private ProgressDialog pDialog;
  private SwipeRefreshLayout swipeRefreshLayout;
  TextView userID, userName, userPhoneNo, userCollege, userEmail, userRegistrationNo,
      userDepartment, userYear, userEvents;
  Button att, scan;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);

    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    swipeRefreshLayout.setColorSchemeResources(R.color.red_primary, R.color.black,
        R.color.blue_primary);

    userID = (TextView) findViewById(R.id.id);
    userName = (TextView) findViewById(R.id.name);
    userCollege = (TextView) findViewById(R.id.coll);
    userPhoneNo = (TextView) findViewById(R.id.phone);
    userEmail = (TextView) findViewById(R.id.email);
    userRegistrationNo = (TextView) findViewById(R.id.reg);
    userDepartment = (TextView) findViewById(R.id.dept);
    userYear = (TextView) findViewById(R.id.yr);
    userEvents = (TextView) findViewById(R.id.part);
    att = (Button) findViewById(R.id.check);
    scan = (Button) findViewById(R.id.scanAnother);

    // Progress dialog
    pDialog = new ProgressDialog(this);
    pDialog.setCancelable(false);

    scan.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent myintent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(myintent);
        finish();
      }
    });

    final String s = getIntent().getStringExtra("code");

    checkDetails(s);

    att.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mark(s);
      }
    });
  }

  //TODO : Use isDeviceOnline function combined with SwipeRefresh (abhish3k)
  private boolean isDeviceOnline() {
    ConnectivityManager connMgr =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }

  /**
   * function to verify login details in mysql db
   */
  private void checkDetails(final String code) {
    // Tag used to cancel the request
    String tag_string_req = "req_login";

    pDialog.setMessage("FETCHING DATA FROM SERVER");
    showDialog();

    StringRequest strReq =
        new StringRequest(Method.POST, AppConfig.URL_API, new Response.Listener<String>() {

          @Override public void onResponse(String response) {
            Log.d(TAG, "Login Response: ");
            hideDialog();

            try {
              JSONObject jObj = new JSONObject(response);
              boolean error = jObj.getBoolean("error");

              // Check for error node in json
              if (!error) {
                // Now store the user in SQLite
                String uid = jObj.getString("uid");
                String name = jObj.getString("name");
                String email = jObj.getString("email");
                String created_at = jObj.getString("phone");
                String college = jObj.getString("college");
                String regis = jObj.getString("regis");
                String dept = jObj.getString("dept");
                String yr = jObj.getString("yr");
                String ev = jObj.getString("evens");
                String at = jObj.getString("att");
                // Sending data to the textViews
                addUse(uid, name, email, created_at, college, regis, dept, yr, ev, at);
              } else {
                // Error in login. Get the error message
                String errorMsg = jObj.getString("error_msg");
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
              }
            } catch (JSONException e) {
              // JSON error
              e.printStackTrace();
              Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),
                  Toast.LENGTH_LONG).show();
            }
          }
        }, new Response.ErrorListener() {
          @Override public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
          }
        }) {
          // This is used for GET request
          @Override protected Map<String, String> getParams() {
            // Posting parameters to login url
            Map<String, String> params = new HashMap<>();
            params.put("email", code);
            return params;
          }
        };
    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
  }

  private void mark(final String code) {
    // Tag used to cancel the request
    String tag_string_req = "req_login";

    pDialog.setMessage("UPDATING DATA ON SERVER");
    showDialog();

    StringRequest strReq =
        new StringRequest(Method.POST, AppConfig.UPDATE_URL, new Response.Listener<String>() {

          @Override public void onResponse(String response) {
            Log.d(TAG, "Login Response: ");
            hideDialog();
            try {
              JSONObject jObj = new JSONObject(response);
              boolean error = jObj.getBoolean("error");

              // Check for error node in json
              if (!error) {
                String msg = jObj.getString("message");
                Toast.makeText(DetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                finish();
              } else {
                // Error in login. Get the error message
                String errorMsg = jObj.getString("error_msg");
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
              }
            } catch (JSONException e) {
              // JSON error
              e.printStackTrace();
              Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),
                  Toast.LENGTH_LONG).show();
            }
          }
        }, new Response.ErrorListener() {

          @Override public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
          }
        }) {
          // This is used for GET request
          @Override protected Map<String, String> getParams() {
            // Posting parameters to login url
            Map<String, String> params = new HashMap<String, String>();
            params.put("mark", code);
            return params;
          }
        };
    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
  }

  private void showDialog() {
    if (!pDialog.isShowing()) pDialog.show();
  }

  private void hideDialog() {
    if (pDialog.isShowing()) pDialog.dismiss();
  }

  // Setting received data in the TextView
  void addUse(String id, String name, String email, String phone, String coll, String regis,
      String dept, String year, String evn, String a) {
    userID.setText(id);
    userName.setText(name);
    userEmail.setText(email);
    userPhoneNo.setText(phone);
    userCollege.setText(coll);
    userRegistrationNo.setText(regis);
    userDepartment.setText(dept);
    userYear.setText(year);

    String[] events = evn.split(",");
    ToggleButton[] tb = new ToggleButton[events.length];
    /*for (int k = 0; k < events.length; k++) {
      LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
      tb[k] = new ToggleButton(getBaseContext());
      tb[k].setLayoutParams(new LinearLayout.LayoutParams());
    }*/
    userEvents.setText(evn);
    if (a.equals("YES")) {
      att.setText("MAKE ENTRY");
    } else if (a.equals("NO")) {
      att.setText("MARK OUTWENT");
    } else {
      att.setVisibility(findViewById(R.id.check).GONE);
    }
    getActionBar().setTitle(id + "-" + name);
  }
}