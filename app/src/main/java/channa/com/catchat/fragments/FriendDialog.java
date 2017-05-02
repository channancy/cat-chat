package channa.com.catchat.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import channa.com.catchat.R;
import channa.com.catchat.activities.ChatActivity;

/**
 * Created by Nancy on 3/30/2017.
 * https://developer.android.com/reference/android/app/DialogFragment.html
 * http://stackoverflow.com/questions/36184359/dialogfragment-shown-as-dialog-resizes-to-full-screen-on-orientation-change
 */

public class FriendDialog extends DialogFragment {

    private final static String TAG = "FriendDialog";

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMembersDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference;

    private Map<String, Boolean> mMemberIDList = new HashMap<>();

    public static FriendDialog newInstance(String userID, String userAvatarUrl, String friendID, String friendName, String friendAvatarUrl) {
        FriendDialog frag = new FriendDialog();
        Bundle args = new Bundle();
        args.putString("userID", userID);
        args.putString("userAvatarUrl", userAvatarUrl);
        args.putString("friendID", friendID);
        args.putString("friendName", friendName);
        args.putString("friendAvatarUrl", friendAvatarUrl);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setStyle(style, theme)
        // Optional custom theme. If 0, an appropriate theme (based on the style) will be selected for you.
        setStyle(DialogFragment.STYLE_NORMAL, 0);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMembersDatabaseReference = mFirebaseDatabase.getReference().child("members");
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String userID = getArguments().getString("userID");
        final String userAvatarUrl = getArguments().getString("userAvatarUrl");
        final String friendID = getArguments().getString("friendID");
        final String friendName = getArguments().getString("friendName");
        final String friendAvatarUrl = getArguments().getString("friendAvatarUrl");

        View v = inflater.inflate(R.layout.fragment_friend_dialog, container, false);
        TextView tvTitle = (TextView) v.findViewById(R.id.tv_friend_dialog_title);
        tvTitle.setText(friendName);

        // Click Chat
        Button button = (Button) v.findViewById(R.id.btn_friend_dialog_chat);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mMembersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Boolean chatExists = false;
                        String membersKey = null;

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.d(TAG, "child key: " + child.getKey());

                                // Check if user keys exist under members
                                if (child.hasChild(userID) && child.hasChild(friendID)) {
                                    Log.d(TAG, "existing chat");

                                    chatExists = true;
                                    membersKey = child.getKey();
                                    break;
                                }
                            }

                        }

                        // Existing chat: user keys exist under members
                        if (chatExists) {
                            // Load messages
                            Bundle args = new Bundle();
                            args.putString("chatID", membersKey);
                            args.putString("userAvatarUrl", userAvatarUrl);
                            args.putString("friendAvatarUrl", friendAvatarUrl);
                            args.putString("friendName", friendName);
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtras(args);
                            dismiss();
                            startActivity(intent);
                        }

                        // New chat: user keys do not exist under members
                        else {
                            Log.d(TAG, "new chat");

                            // Create set of members
                            String key = mMembersDatabaseReference.push().getKey();
                            mMemberIDList.put(userID, true);
                            mMemberIDList.put(friendID, true);

                            // members/chat-id/map-of-members
                            // members
                            // - 123456
                            // -- 234567: true
                            // -- 345678: true
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(key, mMemberIDList);

                            // Update under members
                            mMembersDatabaseReference.updateChildren(childUpdates);

                            // Load messages
                            Bundle args = new Bundle();
                            args.putString("chatID", key);
                            args.putString("userAvatarUrl", userAvatarUrl);
                            args.putString("friendAvatarUrl", friendAvatarUrl);
                            args.putString("friendName", friendName);
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtras(args);
                            dismiss();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }
}
