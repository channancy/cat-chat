package channa.com.catchat.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import butterknife.BindView;
import butterknife.ButterKnife;
import channa.com.catchat.R;
import channa.com.catchat.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsTab extends Fragment {

    private static final String TAG = "FriendsTab";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mContactsDatabaseReference;
    private FirebaseUser user;
    private List<User> mFriendList;

    @BindView(R.id.rv_friend_list) RecyclerView rvFriendList;

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
        Log.d(TAG, "user.getUid(): " + user.getUid());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mContactsDatabaseReference = mFirebaseDatabase.getReference().child("contacts");
        DatabaseReference listRef = mContactsDatabaseReference.child(user.getUid());
        listRef.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot friendFound = iterator.next();
                        User friendDeserialized = friendFound.getValue(User.class);
                        mFriendList.add(friendDeserialized);
                    }

                } catch (NoSuchElementException e) {
                    Log.d(TAG, "onDataChange: No friends yet");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return layout;
    }

}
