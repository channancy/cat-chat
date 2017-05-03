package channa.com.catchat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import channa.com.catchat.R;
import channa.com.catchat.adapters.MessageAdapter;
import channa.com.catchat.models.Chat;
import channa.com.catchat.models.Message;

public class ChatActivity extends AppCompatActivity {

    private final static String TAG = "ChatActivity";

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    // Arbitrary request code values
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mChatsDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    private String mUsername;
    private String mUserID;
    private String mUserAvatarUrl;
    private String mFriendAvatarUrl;
    private String mFriendName;
    private List<Message> mMessageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;

    private MessageAdapter mMessageAdapter;
    @BindView(R.id.rv_message_list)
    RecyclerView rvMessageList;
    @BindView(R.id.btn_photo_picker)
    ImageButton btnPhotoPicker;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_send)
    ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        final String chatID = getIntent().getExtras().getString("chatID");
        mUserAvatarUrl = getIntent().getExtras().getString("userAvatarUrl");
        Log.d(TAG, "user avatar: " + mUserAvatarUrl);
        mFriendAvatarUrl = getIntent().getExtras().getString("friendAvatarUrl");
        mFriendName = getIntent().getExtras().getString("friendName");
        Log.d(TAG, "onCreate: " + chatID);

        // ImagePickerButton shows an image picker to upload a image for a message
        btnPhotoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    btnSend.setEnabled(true);
                } else {
                    btnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etMessage.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Store in messages
                Message message = new Message(mUserAvatarUrl, mUsername, etMessage.getText().toString(), null, mUserID, null);
                mMessagesDatabaseReference.push().setValue(message);

                // Also store in chats
                Chat chat = new Chat(mMessagesDatabaseReference.getKey(), mFriendAvatarUrl, mFriendName, etMessage.getText().toString(), null);
                mChatsDatabaseReference.setValue(chat);

                // Clear input box
                etMessage.setText("");
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // Signed in
                if (user != null) {
                    // Get database references
                    mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(chatID);
                    mChatsDatabaseReference = mFirebaseDatabase.getReference().child("chats").child(chatID);
                    mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");
                    onSignedInInitialize(user.getDisplayName(), user.getUid());

                    // Initialize layout manager and adapter
                    mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                    // List fills its content starting from the bottom of the view
                    mLinearLayoutManager.setStackFromEnd(true);

                    mMessageAdapter = new MessageAdapter(ChatActivity.this, user.getUid());
                    mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount) {
                            super.onItemRangeInserted(positionStart, itemCount);

                            int messageCount = mMessageAdapter.getItemCount();
                            int lastVisiblePosition =
                                    mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                            Log.d(TAG, "lastVisiblePosition: " + lastVisiblePosition);

                            // If the recycler view is initially being loaded or the
                            // user is at the bottom of the list, scroll to the bottom
                            // of the list to show the newly added message.
                            if (lastVisiblePosition == -1 ||
                                    (positionStart >= (messageCount - 1) &&
                                            lastVisiblePosition == (positionStart - 1))) {
                                rvMessageList.scrollToPosition(positionStart);
                                Log.d(TAG, "positionStart: " + positionStart);
                            }
                        }
                    });

                    // Set layout manager and adapter
                    rvMessageList.setLayoutManager(mLinearLayoutManager);
                    rvMessageList.setAdapter(mMessageAdapter);

                    Log.d(TAG, "onAuthStateChanged: signed in: " + user.getDisplayName());
                }
                // Signed out
                else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    // SmartLock saves user's credentials and tries to log them in
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);

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

                    // Add data
                    mMessageAdapter.add(mMessageAdapter.getItemCount(), message);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If equal, the activity being returned from was sign in flow
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Make child named after last path segment of the uri
            // ex) content://local_images/foo/4
            // Child would be named 4
            // Get reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage, returns an UploadTask
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get url from taskSnapshot
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    // Store in messages
                    Message message = new Message(mUserAvatarUrl, mUsername, null, downloadUrl.toString(), mUserID, null);
                    mMessagesDatabaseReference.push().setValue(message);

                    // Also store in chats
                    Chat chat = new Chat(mMessagesDatabaseReference.getKey(), mFriendAvatarUrl, mFriendName, mUsername + " sent a photo.", null);
                    mChatsDatabaseReference.setValue(chat);
                }
            });
        }
    }

    private void onSignedInInitialize(String username, String userID) {
        // Set
        mUsername = username;
        mUserID = userID;

        // Attach listener here because database rules say only authenticated users can access
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        // Unset
        mUsername = null;
        mUserID = null;

        // If do not clear, see messages even though not signed in,
        // also a bug in which see duplicate messages when signing in/out
        detachDatabaseReadListener();
        mMessageAdapter.clear();
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
