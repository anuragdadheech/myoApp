package com.mavericks.myocontroller.network;

import com.mavericks.myocontroller.models.GestureList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Anurag
 */
public class NetworkService {
    private NetworkAPI networkAPI = null;

    public NetworkService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.89.82.180:8084")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        networkAPI = retrofit.create(NetworkAPI.class);
    }

    public void getGestureList(final Callback<GestureList> callback){
        Call<GestureList> call = networkAPI.getGestureList();
        call.enqueue(new Callback<GestureList>() {
            @Override
            public void onResponse(Call<GestureList> call, retrofit2.Response<GestureList> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<GestureList> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
