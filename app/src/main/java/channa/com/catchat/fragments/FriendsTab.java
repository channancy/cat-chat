package channa.com.catchat.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import channa.com.catchat.adapters.FriendListAdapter;
import channa.com.catchat.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsTab extends Fragment {

    private static final String TAG = "FriendsTab";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mContactsDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<User> mFriendList = new ArrayList<>();
    private FriendListAdapter mFriendListAdapter;
    @BindView(R.id.rv_friend_list)
    RecyclerView rvFriendList;
    @BindView(R.id.tv_friend_list_empty)
    TextView tvFriendListEmpty;

    public FriendsTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_friends_tab, container, false);
        ButterKnife.bind(this, layout);

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

                    // Set adapter and layout manager
                    mFriendListAdapter = new FriendListAdapter(getActivity(), userID);
                    rvFriendList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rvFriendList.setAdapter(mFriendListAdapter);

                    mContactsDatabaseReference = mFirebaseDatabase.getReference().child("contacts").child(user.getUid());
                    mContactsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Friend list has contacts
                            if (dataSnapshot.hasChildren()) {
                                tvFriendListEmpty.setVisibility(View.GONE);

                                // Populate RecyclerView
                                attachDatabaseReadListener();

                                Log.d(TAG, "onDataChange: has friends");
                            }
                            // Empty friend list
                            else {
                                tvFriendListEmpty.setVisibility(View.VISIBLE);

                                Log.d(TAG, "onDataChange: no friends");
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
                    mFriendListAdapter.clear();

                    Log.d(TAG, "onAuthStateChanged: signed out: ");
                }
            }
        };

        return layout;
    }

    public void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users").child(dataSnapshot.getKey());
                    mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Deserialize from database to object
                            User friend = dataSnapshot.getValue(User.class);
                            mFriendList.add(friend);
                            Log.d(TAG, "friend name: " + friend.getName());

                            // Add data
                            mFriendListAdapter.add(friend);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

            // Order by friend name
            mContactsDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    public void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mContactsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

        detachDatabaseReadListener();
        mFriendListAdapter.clear();
    }
}
