package channa.com.catchat.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.NoSuchElementException;

import butterknife.BindView;
import butterknife.ButterKnife;
import channa.com.catchat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsTab extends Fragment {

    private static final String TAG = "FriendsTab";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mContactsDatabaseReference;
    private FirebaseUser user;

    @BindView(R.id.tv_friend_name)
    TextView tvFriendName;

    public FriendsTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_friends_tab, container, false);
        ButterKnife.bind(this, layout);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mContactsDatabaseReference = mFirebaseDatabase.getReference().child("contacts");
        DatabaseReference listRef = mContactsDatabaseReference.child(user.getUid());
        listRef.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    DataSnapshot friendFound = dataSnapshot.getChildren().iterator().next();
                    tvFriendName.setText(friendFound.getValue().toString());

                } catch (NoSuchElementException e) {
                    tvFriendName.setText("No friends yet");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return layout;
    }

}
