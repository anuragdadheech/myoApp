package com.mavericks.myocontroller.helpers;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anurag
 */
public class ResourceAccessHelper {
    private static Map<String, String> resourceMap;

    public static String getJsonData(Context context, String filename) throws IOException {
        if(resourceMap == null){
            resourceMap = new HashMap<>();
        }
        if(resourceMap.containsKey(filename)){
            return resourceMap.get(filename);
        }
        InputStream inputStream = context.getAssets().open(filename);
        String jsonStr = IOUtils.toString(inputStream);
        resourceMap.put(filename, jsonStr);
        return jsonStr;
    }
}
