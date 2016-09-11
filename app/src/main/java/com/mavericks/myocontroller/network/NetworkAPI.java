package com.mavericks.myocontroller.network;

import com.mavericks.myocontroller.models.GestureList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author Anurag
 */
public interface NetworkAPI {
    @GET("/language_mapping")
    Call<GestureList> getGestureList();
}
