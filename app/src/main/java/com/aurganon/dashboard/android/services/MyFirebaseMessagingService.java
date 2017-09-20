/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aurganon.dashboard.android.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.aurganon.dashboard.android.app.AppConfig;
import com.aurganon.dashboard.android.utiils.NotificationUtils;
import com.aurganon.dashboard.android.ux.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
  private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

  private NotificationUtils notificationUtils;

  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  // [START receive_message]
  @Override public void onMessageReceived(RemoteMessage remoteMessage) {
    // If the application is in the foreground handle both data and notification messages here.
    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.

    if (remoteMessage == null) return;

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
      handleNotification(remoteMessage.getNotification().getBody());
    }

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

      try {
        JSONObject json = new JSONObject(remoteMessage.getData().toString());
        handleDataMessage(json);
      } catch (Exception e) {
        Log.e(TAG, "Exception: " + e.getMessage());
      }
    }
  }
  // [END receive_message]

  private void handleNotification(String message) {
    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
      // app is in foreground, broadcast the push message
      Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
      pushNotification.putExtra("message", message);
      LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

      // play notification sound
      NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
      notificationUtils.playNotificationSound();
    } else {
      // If the app is in background, firebase itself handles the notification
    }
  }

  private void handleDataMessage(JSONObject json) {
    Log.e(TAG, "push json: " + json.toString());

    try {
      JSONObject data = json.getJSONObject("data");

      String title = data.getString("title");
      String message = data.getString("message");
      boolean isBackground = data.getBoolean("is_background");
      String imageUrl = data.getString("image");
      String timestamp = data.getString("timestamp");
      JSONObject payload = data.getJSONObject("payload");

      Log.e(TAG, "title: " + title);
      Log.e(TAG, "message: " + message);
      Log.e(TAG, "isBackground: " + isBackground);
      Log.e(TAG, "payload: " + payload.toString());
      Log.e(TAG, "imageUrl: " + imageUrl);
      Log.e(TAG, "timestamp: " + timestamp);

      if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
        // app is in foreground, broadcast the push message
        Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
        pushNotification.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

        // play notification sound
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
      } else {
        // app is in background, show the notification in notification tray
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.putExtra("message", message);

        // check for image attachment
        if (TextUtils.isEmpty(imageUrl)) {
          showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
        } else {
          // image is present, show notification with image
          showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp,
              resultIntent, imageUrl);
        }
      }
    } catch (JSONException e) {
      Log.e(TAG, "Json Exception: " + e.getMessage());
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.getMessage());
    }
  }

  /**
   * Showing notification with text only
   */
  private void showNotificationMessage(Context context, String title, String message,
      String timeStamp, Intent intent) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
  }

  /**
   * Showing notification with text and image
   */
  private void showNotificationMessageWithBigImage(Context context, String title, String message,
      String timeStamp, Intent intent, String imageUrl) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
  }
}