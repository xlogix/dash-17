package com.aurganon.dashboard.android.ux;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.aurganon.dashboard.android.R;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
  private static final String TAG = MainActivity.class.getSimpleName();

  private ZXingScannerView mScannerView;
  private static final String FLASH_STATE = "FLASH_STATE";
  private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
  private static final String CAMERA_ID = "CAMERA_ID";
  private static final int ZXING_CAMERA_PERMISSION = 1;
  // Variables
  private boolean mFlash;
  private boolean mAutoFocus;
  private int mCameraId = -1;

  @Override protected void onCreate(Bundle state) {
    super.onCreate(state);
    if (state != null) {
      mFlash = state.getBoolean(FLASH_STATE, false);
      mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
      mCameraId = state.getInt(CAMERA_ID, -1);
    } else {
      mFlash = false;
      mAutoFocus = true;
      mCameraId = -1;
    }
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    askPermission();
    mScannerView = new ZXingScannerView(this);
  }

  @Override public void onPause() {
    super.onPause();
    mScannerView.stopCamera();           // Stop camera on pause
  }

  @Override public void onResume() {
    super.onResume();
    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
    mScannerView.startCamera();          // Start camera on resume
  }

  public void askPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
          ZXING_CAMERA_PERMISSION);
    } else {
      Toast.makeText(this, "#PowerUp", Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case ZXING_CAMERA_PERMISSION:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          return;
        } else {
          Toast.makeText(this, "Please grant camera permission to use the QR Scanner",
              Toast.LENGTH_SHORT).show();
        }
    }
  }

  public void QrScanner(View view) {
    setContentView(mScannerView);

    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
    mScannerView.startCamera();         // Start camera
  }

  @Override public void handleResult(Result rawResult) {
    // Do something with the result here
    Log.d(TAG + " handler", rawResult.getText()); // Prints scan results
    Log.d(TAG + " handler",
        rawResult.getBarcodeFormat().toString()); // Prints the scan format (QR_Code)
    Toast.makeText(this, "Captured Data: " + rawResult.toString(), Toast.LENGTH_LONG)
        .show(); // Shows the result to user
    MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.scan);
    mPlayer.start();

    Intent mainToDetail =
        new Intent(MainActivity.this, DetailsActivity.class).putExtra("code", rawResult.getText());
    startActivity(mainToDetail);
    finish();

    // If you would like to resume scanning, call this method below:
    mScannerView.resumeCameraPreview(this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_about) {
      Intent credits = new Intent(MainActivity.this, AboutActivity.class);
      startActivity(credits);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
