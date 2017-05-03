package channa.com.catchat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * https://firebase.google.com/docs/database/android/read-and-write
 */

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "AddFriendActivity";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    private User mFriend;
    private String mFriendID;

    @BindView(R.id.et_friend_search)
    EditText etFriendSearch;
    @BindView(R.id.btn_friend_search_submit)
    Button btnFriendSearchSubmit;
    @BindView(R.id.btn_add_friend)
    Button btnAddFriend;
    @BindView(R.id.tv_friend_search_result)
    TextView tvFriendSearchResult;
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

                            if (user.getUid().equals(mFriendID)) {
                                tvFriendSearchResult.setText("You cannot add yourself as a friend.");
                                btnAddFriend.setVisibility(View.GONE);
                            }
                            else {
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

                                btnAddFriend.setVisibility(View.VISIBLE);
                            }

                        } catch (NoSuchElementException e) {
                            tvFriendSearchResult.setText(R.string.user_not_found);
                            btnAddFriend.setVisibility(View.GONE);
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
