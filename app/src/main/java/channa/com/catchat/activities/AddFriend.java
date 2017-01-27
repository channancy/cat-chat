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

import butterknife.BindView;
import butterknife.ButterKnife;
import channa.com.catchat.R;
import channa.com.catchat.models.User;

public class AddFriend extends AppCompatActivity {

    private static final String TAG = "AddFriend";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    @BindView(R.id.et_friend_search) EditText etFriendSearch;
    @BindView(R.id.btn_friend_search_submit) Button btnSubmitFriendSearch;
    @BindView(R.id.btn_add_friend) Button btnAddFriend;
    @BindView(R.id.tv_friend_search_result) TextView tvFriendSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        btnSubmitFriendSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsersDatabaseReference.orderByChild("email").equalTo(etFriendSearch.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            DataSnapshot friendFound = dataSnapshot.getChildren().iterator().next();
                            User friend = friendFound.getValue(User.class);
                            tvFriendSearchResult.setText(friend.getName());
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
    }
}
