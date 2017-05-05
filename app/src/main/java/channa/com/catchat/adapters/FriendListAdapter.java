package channa.com.catchat.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import channa.com.catchat.R;
import channa.com.catchat.fragments.FriendDialog;
import channa.com.catchat.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nancy on 3/28/2017.
 * http://stackoverflow.com/questions/18689727/getfragmentmanager-from-arrayadapter
 * http://stackoverflow.com/questions/26466877/how-to-create-context-menu-for-recyclerview/29280916
 * http://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private final static String TAG = "FriendListAdapter";

    private static final int DELETE = 0;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mContactsDatabaseReference;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String mUserID;
    private List<User> mFriendList = new ArrayList<>();

    public FriendListAdapter(Context context, String userID) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mUserID = userID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_friend, parent, false);
        FriendListAdapter.ViewHolder holder = new FriendListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User friend = mFriendList.get(position);
        holder.friendName.setText(friend.getName());

        if (friend.getAvatarUrl() != null) {
            Glide.with(mContext).load(friend.getAvatarUrl()).into(holder.friendAvatar);
        }
        else {
            Glide.with(mContext).load(R.drawable.cat_silhouette_head).into(holder.friendAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView friendAvatar;
        TextView friendName;

        public ViewHolder(View itemView) {
            super(itemView);

            friendAvatar = (CircleImageView) itemView.findViewById(R.id.iv_friend_avatar);
            friendName = (TextView) itemView.findViewById(R.id.tv_friend_name);

            itemView.setOnClickListener(this);

            // Context menu for remove friend
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(R.string.remove_friend).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            User friend = mFriendList.get(getAdapterPosition());
                            final String friendID = friend.getId();

                            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                            alert.setMessage(R.string.are_you_sure_you_want_to_remove_this_friend);
                            alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "onClick: yes");

                                    mFirebaseDatabase = FirebaseDatabase.getInstance();
                                    mContactsDatabaseReference = mFirebaseDatabase.getReference().child("contacts");
                                    mContactsDatabaseReference.child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get friend list
                                            Map<String, Boolean> friendList = (Map<String, Boolean>) dataSnapshot.getValue();
                                            // Remove friend
                                            friendList.remove(friendID);

                                            // Update database
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(mUserID, friendList);
                                            mContactsDatabaseReference.updateChildren(childUpdates);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    dialog.dismiss();
                                }
                            });
                            alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "onClick: no");
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                            
                            return true;
                        }
                    });
                }
            });
        }

        // Show dialog with friend details
        @Override
        public void onClick(View view) {
            User friend = mFriendList.get(getAdapterPosition());

            Log.d(TAG, "onClick: " + friend.getName());
            showDialog(mUserID, friend.getId(), friend.getName(), friend.getAvatarUrl());
        }
    }

    public void add(User friend) {
        mFriendList.add(friend);
        Collections.sort(mFriendList);
        notifyDataSetChanged();
    }

    public void remove(String friendID) {
        for (User friend : mFriendList) {
            if (friend.getId().equals(friendID)) {
                mFriendList.remove(friend);
                break;
            }
        }

        Collections.sort(mFriendList);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mFriendList.size() > 0) {
            mFriendList.clear();
        }

        notifyDataSetChanged();
    }

    private void showDialog(String userID, String friendID, String friendName, String friendAvatarUrl) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        Fragment prev = ((Activity) mContext).getFragmentManager().findFragmentByTag("FriendDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = FriendDialog.newInstance(userID, friendID, friendName, friendAvatarUrl);
        newFragment.show(((Activity) mContext).getFragmentManager(), "FriendDialog");
    }
}
