package com.mavericks.myocontroller.helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mavericks.myocontroller.models.GestureList;

/**
 * @author Anurag
 */
public class ResponseTranslator {
    private static ResponseTranslator sharedInstance;
    private Gson gson;

    private ResponseTranslator() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .create();
        }
    }

    public static synchronized ResponseTranslator getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ResponseTranslator();
        }
        return sharedInstance;
    }

    public GestureList getGestureList(JsonObject gesturesJson) {
        try {
            return ResponseTranslator.getSharedInstance().gson.fromJson(gesturesJson, GestureList.class);
        } catch (Exception e) {
            Log.e(ResponseTranslator.class.getSimpleName() + ": IO error", "IO error: " + e.getMessage());
        }
        return null;
    }

    public interface ResponseKeys {
        String DATA = "data";
        String META = "meta";
    }
}
