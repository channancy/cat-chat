package channa.com.catchat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import butterknife.BindView;
import butterknife.ButterKnife;
import channa.com.catchat.R;
import channa.com.catchat.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

/**
 * https://firebase.google.com/docs/database/android/read-and-write
 */

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "AddFriendActivity";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mContactsDatabaseReference;

    private User mFriend;
    private String mFriendID;

    @BindView(R.id.et_friend_search)
    EditText etFriendSearch;
    @BindView(R.id.btn_friend_search_submit)
    ImageView btnFriendSearchSubmit;
    @BindView(R.id.btn_add_friend)
    Button btnAddFriend;
    @BindView(R.id.tv_friend_search_result)
    TextView tvFriendSearchResult;
    @BindView(R.id.tv_friend_search_result_notes)
    TextView tvFriendSearchResultNotes;
    @BindView(R.id.iv_friend_search_avatar)
    CircleImageView ivFriendSearchAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mContactsDatabaseReference = mFirebaseDatabase.getReference().child("contacts");

        final FirebaseUser user = mFirebaseAuth.getCurrentUser();

        // Search for friend by email
        btnFriendSearchSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsersDatabaseReference.orderByChild("email").equalTo(etFriendSearch.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            DataSnapshot friendFound = dataSnapshot.getChildren().iterator().next();
                            mFriend = friendFound.getValue(User.class);
                            mFriendID = friendFound.getKey();
                            String friendAvatarUrl;

                            // Name
                            tvFriendSearchResult.setText(mFriend.getName());

                            // Use uploaded profile picture
                            if (mFriend.getAvatarUrl() != null) {
                                friendAvatarUrl = mFriend.getAvatarUrl();

                            }
                            // Otherwise, use default profile picture
                            else {
                                friendAvatarUrl = "http://goo.gl/gEgYUd";
                            }

                            Glide.with(getApplicationContext()).load(friendAvatarUrl).into(ivFriendSearchAvatar);

                            // Searched for self
                            if (user.getUid().equals(mFriendID)) {
                                tvFriendSearchResult.setVisibility(View.VISIBLE);
                                tvFriendSearchResultNotes.setText(R.string.you_cannot_add_yourself_as_a_friend);
                                tvFriendSearchResultNotes.setVisibility(View.VISIBLE);
                                ivFriendSearchAvatar.setVisibility(View.VISIBLE);

                                // Hide add button
                                btnAddFriend.setVisibility(GONE);
                            }
                            // Searched for other user
                            else {
                                // Check if already friends
                                mContactsDatabaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Already friends
                                        if (dataSnapshot.hasChild(mFriendID)) {
                                            tvFriendSearchResult.setVisibility(View.VISIBLE);
                                            tvFriendSearchResultNotes.setText(R.string.already_friends);
                                            tvFriendSearchResultNotes.setVisibility(View.VISIBLE);
                                            ivFriendSearchAvatar.setVisibility(View.VISIBLE);

                                            // Hide add button
                                            btnAddFriend.setVisibility(GONE);
                                        }
                                        // Not friends yet
                                        else {
                                            // Clear and hide message that informs user
                                            tvFriendSearchResult.setVisibility(View.VISIBLE);
                                            tvFriendSearchResultNotes.setText("");
                                            tvFriendSearchResultNotes.setVisibility(GONE);
                                            ivFriendSearchAvatar.setVisibility(View.VISIBLE);

                                            // Show add button
                                            btnAddFriend.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        // User not found
                        catch (NoSuchElementException e) {
                            tvFriendSearchResult.setText(R.string.user_not_found);
                            tvFriendSearchResultNotes.setText("");
                            tvFriendSearchResultNotes.setVisibility(GONE);
                            ivFriendSearchAvatar.setVisibility(GONE);

                            // Hide add button
                            btnAddFriend.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        // Add friend to contacts
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mContactsDatabaseReference = mFirebaseDatabase.getReference().child("contacts");

                mContactsDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Friend list has contacts
                        if (dataSnapshot.exists()) {
                            // Get friend list
                            Map<String, Boolean> friendList = (Map<String, Boolean>) dataSnapshot.getValue();
                            // Add friend
                            friendList.put(mFriendID, true);

                            // Update database
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(user.getUid(), friendList);
                            mContactsDatabaseReference.updateChildren(childUpdates);
                        }
                        // Empty friend list
                        else {
                            // Create friend list
                            Map<String, Boolean> friendList = new HashMap<String, Boolean>();
                            // Add friend
                            friendList.put(mFriendID, true);

                            // Save to database
                            mContactsDatabaseReference.child(user.getUid()).setValue(friendList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
