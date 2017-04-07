package channa.com.catchat.fragments;

import android.app.DialogFragment;
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

    public static FriendDialog newInstance(String userID, String friendID, String friendName, String friendAvatar) {
        FriendDialog frag = new FriendDialog();
        Bundle args = new Bundle();
        args.putString("userID", userID);
        args.putString("friendID", friendID);
        args.putString("friendName", friendName);
        args.putString("friendAvatar", friendAvatar);
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
        final String friendID = getArguments().getString("friendID");
        String friendName = getArguments().getString("friendName");
        String friendAvatar = getArguments().getString("friendAvatar");

        View v = inflater.inflate(R.layout.fragment_friend_dialog, container, false);
        TextView tvTitle = (TextView) v.findViewById(R.id.tv_friend_dialog_title);
        tvTitle.setText(friendName);

        // Click Chat
        Button button = (Button) v.findViewById(R.id.btn_friend_dialog_chat);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "clicked chat");

                mMembersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "userID: " + userID);
                        Log.d(TAG, "friendID: " + friendID);

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                            Log.d(TAG, "child: " + child.getKey());

                                // User keys exist under members
                                if (child.hasChild(userID) && child.hasChild(friendID)) {
                                    Log.d(TAG, "existing chat");

                                    // Load messages
                                }
                                // Users keys do not exist under members
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

                                    // Update under messages
                                    mMessagesDatabaseReference.updateChildren(childUpdates);
                                }
                            }

                        }
                        else {
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

                            // Update under messages
                            mMessagesDatabaseReference.updateChildren(childUpdates);
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

    //    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        String title = getArguments().getString("title");
//
//        // Get the layout inflater
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.fragment_friend_dialog, null))
//                .setTitle(title)
//                // Add action buttons
//                .setPositiveButton(R.string.chat, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.d(TAG, "clicked chat");
//                    }
//                });
//        return builder.create();
//    }
}
