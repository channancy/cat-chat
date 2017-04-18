package channa.com.catchat.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import channa.com.catchat.R;
import channa.com.catchat.fragments.FriendDialog;
import channa.com.catchat.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nancy on 3/28/2017.
 * http://stackoverflow.com/questions/18689727/getfragmentmanager-from-arrayadapter
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private final static String TAG = "FriendListAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String userID;
    private List<User> mFriendList = new ArrayList<>();

    public FriendListAdapter(Context context, String userID) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.userID = userID;
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
            Glide.with(mContext).load("http://goo.gl/gEgYUd").into(holder.friendAvatar);
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
        }

        @Override
        public void onClick(View view) {
            User friend = mFriendList.get(getAdapterPosition());

            Log.d(TAG, "onClick: " + friend.getName());
            showDialog(userID, friend.getId(), friend.getName(), friend.getAvatarUrl());
        }
    }

    public void setFriendList(List<User> friendList) {
        mFriendList = friendList;
        notifyDataSetChanged();
    }

    public void clear() {
        if (mFriendList.size() > 0) {
            mFriendList.clear();
        }

        notifyDataSetChanged();
    }

    private void showDialog(String userID, String friendID, String friendName, String friendAvatar) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        Fragment prev = ((Activity) mContext).getFragmentManager().findFragmentByTag("FriendDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = FriendDialog.newInstance(userID, friendID, friendName, friendAvatar);
        newFragment.show(((Activity) mContext).getFragmentManager(), "FriendDialog");
    }
}
