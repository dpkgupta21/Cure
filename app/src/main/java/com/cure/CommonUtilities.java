package com.cure;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    // give your server registration url here
    // public static final String SERVER_URL =
    // "http://192.168.0.105/gcm/gcm.php?shareRegId=1";

    // Google project id
    public static final String SENDER_ID = "731647998770";
    //Api KEY
    public static final String API_KEY = "AIzaSyCt3KdveR3oUWmB1ceZndIiHe3oJBY-ibI";
    /**
     * Tag used on log messages.
     */
    public static final String TAG = "CURE GCM";

    public static final String DISPLAY_MESSAGE_ACTION = "com.cure.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_TYPE = "type";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by the
     * UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message,String type) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_TYPE,type);
        context.sendBroadcast(intent);
    }
}
