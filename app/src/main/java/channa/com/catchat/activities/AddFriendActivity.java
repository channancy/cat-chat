package channa.com.catchat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Map<String, Boolean> mFriendIDList = new HashMap<>();

    @BindView(R.id.et_friend_search) EditText etFriendSearch;
    @BindView(R.id.btn_friend_search_submit) Button btnFriendSearchSubmit;
    @BindView(R.id.btn_add_friend) Button btnAddFriend;
    @BindView(R.id.tv_friend_search_result) TextView tvFriendSearchResult;

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
                            tvFriendSearchResult.setText(mFriend.getName());
                            btnAddFriend.setVisibility(View.VISIBLE);

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
                DatabaseReference mContactsDatabaseReference = mFirebaseDatabase.getReference().child("contacts");

                mFriendIDList.put(mFriendID, true);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(user.getUid(), mFriendIDList);

                mContactsDatabaseReference.updateChildren(childUpdates);
            }
        });
    }
}
