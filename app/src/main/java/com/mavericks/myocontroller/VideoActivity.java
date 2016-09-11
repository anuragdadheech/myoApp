package com.mavericks.myocontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mavericks.myocontroller.adapters.VideoAdapter;
import com.mavericks.myocontroller.helpers.ResourceAccessHelper;
import com.mavericks.myocontroller.helpers.ResponseTranslator;
import com.mavericks.myocontroller.models.Video;
import com.mavericks.myocontroller.models.VideoList;
import com.mavericks.myocontroller.network.NetworkService;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    private VideoList videos = new VideoList();
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (null == recyclerView || null == videoAdapter) {
            recyclerView = (RecyclerView) findViewById(R.id.rv_video);
            videoAdapter = new VideoAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(videoAdapter);
        }
        getVideoList();
    }

    private void getVideoList() {
        NetworkService networkService = new NetworkService();
        networkService.getVideoList(new Callback<VideoList>() {
            @Override
            public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                videos = (VideoList) response.body();
                videoAdapter.setVideos(videos);
            }

            @Override
            public void onFailure(Call<VideoList> call, Throwable t) {
                videos = getLocalVideoList();
                videoAdapter.setVideos(videos);
            }
        });

    }

    private VideoList getLocalVideoList() {
        VideoList videoList = new VideoList();
        try {
            String json = ResourceAccessHelper.getJsonData(this, "videos.json");
            JsonArray jsonObject = new JsonParser().parse(json).getAsJsonArray();
            videoList = ResponseTranslator.getSharedInstance().getVideoList(jsonObject);
        } catch (IOException e) {
            Log.e("Error", MainActivity.class.getSimpleName());
        }
        return videoList;
    }
}
