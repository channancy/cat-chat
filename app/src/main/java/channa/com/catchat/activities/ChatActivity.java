package channa.com.catchat.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import channa.com.catchat.R;
import channa.com.catchat.adapters.MessageAdapter;
import channa.com.catchat.models.Message;

public class ChatActivity extends AppCompatActivity {

    private final static String TAG = "ChatActivity";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<Message> mMessageList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;
    @BindView(R.id.rv_message_list)
    RecyclerView rvMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        String chatID = getIntent().getExtras().getString("chatID");
        Log.d(TAG, "onCreate: " + chatID);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // Signed in
                if (user != null) {
                    final String userID = user.getUid();

                    mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
                    mMessagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Friend list has contacts
                            if (dataSnapshot.hasChildren()) {
                                // tvFriendListEmpty.setVisibility(View.GONE);

                                // Initialize adapter and set layout manager
                                mMessageAdapter = new MessageAdapter(ChatActivity.this);
                                rvMessageList.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

                                // Populate RecyclerView
                                attachDatabaseReadListener();

                                Log.d(TAG, "onDataChange: has messages");
                            }
                            // Empty friend list
                            else {
                                // tvFriendListEmpty.setVisibility(View.VISIBLE);

                                Log.d(TAG, "onDataChange: no messages");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Log.d(TAG, "onAuthStateChanged: signed in: " + user.getDisplayName());
                }
                // Signed out
                else {
                    detachDatabaseReadListener();
                    mMessageAdapter.clear();

                    Log.d(TAG, "onAuthStateChanged: signed out: ");
                }
            }
        };
    }

    public void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // Deserialize from database to object
                    Message message = dataSnapshot.getValue(Message.class);
                    mMessageList.add(message);
                    Log.d(TAG, "message text: " + message.getText());

                    // Set data and adapter
                    mMessageAdapter.setMessageList(mMessageList);
                    rvMessageList.setAdapter(mMessageAdapter);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    public void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detach auth state listener
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        // If signed in, database listener is attached so detach and clear adapter here
        // Also ensures that when activity destroyed (even when nothing to do with sign out like app rotation), still cleanup
        detachDatabaseReadListener();
        mMessageAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Attach auth state listener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
