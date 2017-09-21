package com.aurganon.dashboard.android.app;

public class AppConfig {
  // Server APIs
  public static String URL_API = "http://code.aurganon.com/workspace/app/qr.php";
  public static String UPDATE_URL = "http://code.aurganon.com/workspace/app/update.php";

  // NOTIFICATIONS
  // global topic to receive app wide push notifications
  public static final String TOPIC_GLOBAL = "global";

  // broadcast receiver intent filters
  public static final String REGISTRATION_COMPLETE = "registrationComplete";
  public static final String PUSH_NOTIFICATION = "pushNotification";

  // id to handle the notification in the notification tray
  public static final int NOTIFICATION_ID = 100;
  public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

}
