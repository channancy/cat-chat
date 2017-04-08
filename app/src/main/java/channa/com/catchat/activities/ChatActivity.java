package channa.com.catchat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import channa.com.catchat.R;

public class ChatActivity extends AppCompatActivity {

    private final static String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String chatID = getIntent().getExtras().getString("chatID");
        Log.d(TAG, "onCreate: " + chatID);
    }
}
