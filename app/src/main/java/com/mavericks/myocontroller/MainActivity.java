package com.mavericks.myocontroller;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mavericks.myocontroller.adapters.ConversationAdapter;
import com.mavericks.myocontroller.helpers.ResourceAccessHelper;
import com.mavericks.myocontroller.helpers.ResponseTranslator;
import com.mavericks.myocontroller.models.GestureList;
import com.mavericks.myocontroller.models.Message;
import com.mavericks.myocontroller.models.Motion;
import com.mavericks.myocontroller.network.NetworkService;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final float SWIPE_THRESHOLD = 15;
    private static final float ROTATE_THRESHOLD = 15;
    private static final float THUMP_THRESHOLD = 25;
    private static final int SPEECH_REQUEST_CODE = 5010;

    private Button btnLearn;
    private ViewSwitcher viewSwitcher;
    private RecyclerView recyclerView;
    private ConversationAdapter conversationAdapter;
    private TextView deviceInfo;

    TextToSpeech t2s;

    private boolean resetValues = false;
    double rollDefault = 50;
    double pitchDefault = 50;
    double yawDefault = 50;
    Pose currentPose;
    GestureList gestureList;

    private String currentSpokenSentence = "";
    private String currentListeningSentence = "";
    private ArrayList<Message> conversation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.vs);
        btnLearn = (Button) findViewById(R.id.btn_learn);
        deviceInfo = (TextView) findViewById(R.id.device_info);
        btnLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(i);
            }
        });
        if (null == recyclerView || null == conversationAdapter) {
            recyclerView = (RecyclerView) findViewById(R.id.rv_chat);
            conversationAdapter = new ConversationAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(conversationAdapter);
        }
        deviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanActionSelected();
            }
        });
        getGestureList();
        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e(MainActivity.class.getSimpleName(), "Could not initialize the Hub.");
            finish();
            return;
        }
        t2s = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t2s.setLanguage(Locale.US);
                }
            }
        });
    }

    private DeviceListener mListener = new AbstractDeviceListener() {
        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
//            t3.setText("Connected");
//            t3.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
//            t3.setText("Disconnected");
//            t3.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
//            deviceInfo.setText("Double tap to start");
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
//            deviceInfo.setText("Sync Device");
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
//            t4.setText("Unlocked");
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
//            t4.setText("Locked");
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            currentPose = pose;
            switch (pose) {
                case UNKNOWN:
//                    t1.setText("Unknown");
                    break;
                case REST:
//                    t1.setText("Rest");
                    break;
                case DOUBLE_TAP:
                    resetDegreesOfFreedom();
                    myo.vibrate(Myo.VibrationType.MEDIUM);
                    if (null != viewSwitcher) {
                        viewSwitcher.setDisplayedChild(1);
                    }
                    if (StringUtils.isNotEmpty(currentSpokenSentence)) {
                        Bundle params = new Bundle();
                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
                        t2s.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String s) {

                            }

                            @Override
                            public void onDone(String s) {
                                showGoogleInputDialog();

                            }

                            @Override
                            public void onError(String s) {

                            }
                        });
                        t2s.speak(currentSpokenSentence, TextToSpeech.QUEUE_FLUSH, params, "uniqueId");
                        Message msg = new Message(currentSpokenSentence, true);
                        conversation.add(msg);
                        conversationAdapter.setConversation(conversation);
                        recyclerView.smoothScrollToPosition(conversationAdapter.getItemCount() - 1);
                        currentSpokenSentence = "";

                    }
                    String restText = "";
                    switch (myo.getArm()) {
                        case LEFT:
                            restText = "Left Arm double tap";
                            break;
                        case RIGHT:
                            restText = "Right arm double tap";
                            break;
                    }
//                    t1.setText(restText);
                    break;
                case FIST:
//                    t1.setText("Fist");
                    break;
                case WAVE_IN:
//                    t1.setText("Wave in");
                    break;
                case WAVE_OUT:
//                    t1.setText("Wave out");
                    break;
                case FINGERS_SPREAD:
//                    t1.setText("Finger spread");
                    break;
            }
        }

        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
            super.onAccelerometerData(myo, timestamp, accel);

        }

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            super.onOrientationData(myo, timestamp, rotation);
            Quaternion normalized = rotation.normalized();
            double pitch = Math.asin(2.0f * (normalized.w() * normalized.y() - normalized.z() * normalized.x()));
//            double pitchW = ((pitch + Math.PI / 2.0) / Math.PI * 16);

            double roll = Math.atan2(2.0f * (normalized.w() * normalized.x() + normalized.y() * normalized.z()), 1.0f - 2.0f * (normalized.x() * normalized.x() + normalized.y() * normalized.y()));
//            double rollW = ((roll + Math.PI / 2.0) / Math.PI * 16);

//            double yaw = normalized.w();
            double yaw = Math.atan2(2.0f * (normalized.w() * normalized.z() + normalized.x() * normalized.y()), 1.0f - 2.0f * (normalized.y() * normalized.y() + normalized.z() * normalized.z()));
//            double yawW = ((yaw + Math.PI / 2.0) / Math.PI * 16);

            double rollW = ((roll + Math.PI) / (Math.PI * 2.0) * 100);
            double pitchW = ((pitch + Math.PI / 2.0) / Math.PI * 100);
            double yawW = ((yaw + Math.PI) / (Math.PI * 2.0) * 100);
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            if (resetValues) {
                rollDefault = rollW;
                pitchDefault = pitchW;
                yawDefault = yawW;
                resetValues = false;

            }
//            thump.setText("Thump: " + String.valueOf(pitchW) + "\n Thump Default: " + pitchDefault);
//            rotate.setText("Rotate: " + String.valueOf(rollW) + "\n Rotate Default: " + rollDefault);
//            swipe.setText("Swipe: " + String.valueOf(yawW) + "\n Swipe Default: " + yawDefault);

            Motion m = getAction(rollW, pitchW, yawW);
            if (m != Motion.NONE && currentPose != Pose.UNKNOWN && currentPose != Pose.DOUBLE_TAP && currentPose != Pose.REST) {
                if (null != gestureList && null != currentPose && null != gestureList.get(m.name())) {
                    String text = gestureList.get(m.name()).get(currentPose.name());
                    if (StringUtils.isNotEmpty(text)) {
                        currentSpokenSentence += text + " ";
                        currentPose = Pose.UNKNOWN;
                    }
                }

            }

//            motion.setText(m.name());

        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // We don't want any callbacks when the Activity is in background, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Hub.getInstance().addListener(mListener);
        Hub.getInstance().setLockingPolicy(Hub.LockingPolicy.NONE);
        if(null != Hub.getInstance().getConnectedDevices() && Hub.getInstance().getConnectedDevices().size() > 0) {
            deviceInfo.setText("Double tap to start");
        }
    }

    private void resetDegreesOfFreedom() {
        resetValues = true;
    }

    private Motion getAction(double roll, double pitch, double yaw) {
        if (Math.abs(pitch - pitchDefault) > THUMP_THRESHOLD) {
            return isDirectionPositive(pitch, pitchDefault) ? Motion.THUMP_UP : Motion.THUMP_DOWN;
        } else if (Math.abs(yaw - yawDefault) > SWIPE_THRESHOLD) {
            return isDirectionPositive(yaw, yawDefault) ? Motion.SWIPE_LEFT : Motion.SWIPE_RIGHT;
        } else if (Math.abs(roll - rollDefault) > ROTATE_THRESHOLD) {
            return isDirectionPositive(roll, rollDefault) ? Motion.ROTATE_LEFT : Motion.ROTATE_RIGHT;
        }
        return Motion.NONE;
    }

    private boolean isDirectionPositive(double a, double b) {
        double diff1 = mod(a - b, 100);
        return diff1 < 49;
    }

    private double mod(double x, double y) {
        double result = x % y;
        return result < 0? result + y : result;
    }

    private GestureList getLocalGestureList() {
        GestureList gestureList = new GestureList();
        try {
            String json = ResourceAccessHelper.getJsonData(this, "mapping.json");
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            gestureList = ResponseTranslator.getSharedInstance().getGestureList(jsonObject);
        } catch (IOException e) {
            Log.e("Error", MainActivity.class.getSimpleName());
        }
        return gestureList;
    }

    private void getGestureList() {
        NetworkService networkService = new NetworkService();
        networkService.getGestureList(new Callback<GestureList>() {
            @Override
            public void onResponse(Call<GestureList> call, Response<GestureList> response) {
                gestureList = (GestureList) response.body();
            }

            @Override
            public void onFailure(Call<GestureList> call, Throwable t) {
                gestureList = getLocalGestureList();
            }
        });
    }

    public void showGoogleInputDialog() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(2000));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Your device is not supported!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Message msg = new Message(result.get(0), false);
                    conversation.add(msg);
                    conversationAdapter.setConversation(conversation);
                    recyclerView.smoothScrollToPosition(conversationAdapter.getItemCount() - 1);

                }
                break;
            }

        }
    }

}