package channa.com.catchat.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import channa.com.catchat.R;
import channa.com.catchat.fragments.FriendDialog;
import channa.com.catchat.models.User;

/**
 * Created by Nancy on 3/28/2017.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private final static String TAG = "FriendListAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<User> mFriendList;

    public FriendListAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
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
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView friendAvatar;
        TextView friendName;

        public ViewHolder(View itemView) {
            super(itemView);

            friendAvatar = (ImageView) itemView.findViewById(R.id.iv_friend_avatar);
            friendName = (TextView) itemView.findViewById(R.id.tv_friend_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            User friend = mFriendList.get(getAdapterPosition());

            Log.d(TAG, "onClick: " + friend.getName());
            showDialog();
        }
    }

    public void setFriendList(List<User> friendList) {
        mFriendList = friendList;
        notifyDataSetChanged();
    }

    public void clear() {
        if (mFriendList != null) {
            mFriendList.clear();
        }

        notifyDataSetChanged();
    }

    public void showDialog() {
        DialogFragment newFragment = FriendDialog.newInstance(R.string.friend);
        newFragment.show(((Activity) mContext).getFragmentManager(), "dialog");
    }
}
