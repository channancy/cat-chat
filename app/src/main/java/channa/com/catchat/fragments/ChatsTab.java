package channa.com.catchat.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import channa.com.catchat.adapters.ChatListAdapter;
import channa.com.catchat.models.Chat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsTab extends Fragment {

    private static final String TAG = "ChatsTab";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatsDatabaseReference;
    private DatabaseReference mMembersDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<String> mChatIDList = new ArrayList<>();
    private ChatListAdapter mChatListAdapter;
    @BindView(R.id.rv_chat_list)
    RecyclerView rvChatList;
    @BindView(R.id.tv_chat_list_empty)
    TextView tvChatListEmpty;

    public ChatsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_chats_tab, container, false);
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
                    mChatListAdapter = new ChatListAdapter(getActivity(), userID);
                    rvChatList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rvChatList.setAdapter(mChatListAdapter);

                    mChatsDatabaseReference = mFirebaseDatabase.getReference().child("chats");

                    // Retrieve list of chat IDs by checking if user ID exists under chat IDs in members database
                    mMembersDatabaseReference = mFirebaseDatabase.getReference().child("members");
                    mMembersDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Chat list has chats
                            tvChatListEmpty.setVisibility(View.GONE);

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.hasChild(userID) && !mChatIDList.contains(child.getKey())) {
                                    mChatIDList.add(child.getKey());
//                                    Log.d(TAG, "Added chat id: " + child.getKey());
                                }
                            }

                            attachDatabaseReadListener();

                            // Empty chat list
                            if (mChatIDList.size() == 0) {
                                tvChatListEmpty.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

//                    Log.d(TAG, "onAuthStateChanged: signed in: " + user.getDisplayName());
                }
                // Signed out
                else {
                    detachDatabaseReadListener();

                    mChatIDList.clear();

                    if (mChatListAdapter != null) {
                        mChatListAdapter.clear();
                    }

//                    Log.d(TAG, "onAuthStateChanged: signed out: ");
                }
            }
        };

        return layout;
    }

    // Add chats to adapter by checking for matches between chat ID list and chat IDs in chats database
    public void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (mChatIDList.contains(dataSnapshot.getKey())) {
//                        Log.d(TAG, "Contains chat ID: " + dataSnapshot.getKey());
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        mChatListAdapter.add(chat);
                    }
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

            mChatsDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    public void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mChatsDatabaseReference.removeEventListener(mChildEventListener);
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

        mChatIDList.clear();

        if (mChatListAdapter != null) {
            mChatListAdapter.clear();
        }
    }
}
