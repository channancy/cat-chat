package channa.com.catchat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.NoSuchElementException;

import channa.com.catchat.R;
import channa.com.catchat.models.User;

public class AddFriend extends AppCompatActivity {

    private static final String TAG = "AddFriend";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    private EditText mEtFriendSearch;
    private Button mBtnSearch;
    private TextView mTvFriendSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        mEtFriendSearch = (EditText) findViewById(R.id.et_friend_search);
        mBtnSearch = (Button) findViewById(R.id.btn_submit_friend_search);
        mTvFriendSearchResult = (TextView) findViewById(R.id.tv_friend_search_result);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsersDatabaseReference.orderByChild("email").equalTo(mEtFriendSearch.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            DataSnapshot friendFound = dataSnapshot.getChildren().iterator().next();
                            User friend = friendFound.getValue(User.class);
                            mTvFriendSearchResult.setText(friend.getName());

                        } catch (NoSuchElementException e) {
                            mTvFriendSearchResult.setText(R.string.user_not_found);
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
