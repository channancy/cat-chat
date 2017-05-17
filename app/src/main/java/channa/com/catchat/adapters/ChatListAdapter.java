package channa.com.catchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import channa.com.catchat.R;
import channa.com.catchat.activities.ChatActivity;
import channa.com.catchat.models.Chat;
import channa.com.catchat.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nancy on 4/26/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private final static String TAG = "ChatListAdapter";

    private FirebaseDatabase mFirebaseDatabase;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Chat> mChatList = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat;
    private TimeZone mTimeZone;
    private String mUserID;
    private String mFriendID;
    private User mFriend;

    public ChatListAdapter(Context context, String userID) {
        mContext = context;
        mUserID = userID;
        mLayoutInflater = LayoutInflater.from(context);

        mSimpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        mTimeZone = TimeZone.getDefault();
        mSimpleDateFormat.setTimeZone(this.mTimeZone);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_chat, parent, false);
        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Chat chat = mChatList.get(position);
        holder.lastMessage.setText(chat.getLastMessage());

        Date date = new Date(chat.getDateCreatedLong());
        String formattedDate = mSimpleDateFormat.format(date);
        holder.lastTimestamp.setText(formattedDate);

        // Find member in chat who is not the logged in user (user's friend)
        mFirebaseDatabase.getReference().child("members").child(chat.getChatID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (!child.getKey().equals(mUserID)) {
                        mFriendID = child.getKey();
                        break;
                    }
                }

                // Get user's friend's avatar
                mFirebaseDatabase.getReference().child("users").child(mFriendID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFriend = dataSnapshot.getValue(User.class);

                        // Use uploaded profile picture
                        if (mFriend.getAvatarUrl() != null) {
                            Glide.with(mContext).load(mFriend.getAvatarUrl()).into(holder.avatarUrl);

                        }
                        // Otherwise, use default profile picture
                        else {
                            Glide.with(mContext).load(R.drawable.cat_silhouette_head).into(holder.avatarUrl);
                        }

                        // Set friend name
                        holder.title.setText(mFriend.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView avatarUrl;
        TextView title;
        TextView lastMessage;
        TextView lastTimestamp;

        public ViewHolder(View itemView) {
            super(itemView);

            avatarUrl = (CircleImageView) itemView.findViewById(R.id.iv_friend_avatar_chat);
            title = (TextView) itemView.findViewById(R.id.tv_chat_title);
            lastMessage = (TextView) itemView.findViewById(R.id.tv_chat_last_message);
            lastTimestamp = (TextView) itemView.findViewById(R.id.tv_chat_last_timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Chat chat = mChatList.get(getAdapterPosition());

            // Load messages
            Bundle args = new Bundle();
            args.putString("chatID", chat.getChatID());
            args.putString("chatName", mFriend.getName());
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtras(args);
            mContext.startActivity(intent);
        }
    }

    public void add(Chat chat) {
        mChatList.add(chat);
        
        Collections.sort(mChatList, new Comparator<Chat>() {
            @Override
            public int compare(Chat chat1, Chat chat2) {
//                Log.d(TAG, "compare: ");
                
                if (chat1.getDateCreatedLong() == chat2.getDateCreatedLong()) {
                    return 0;
                }
                else if (chat1.getDateCreatedLong() < chat2.getDateCreatedLong()){
                    return 1;
                }
                else {
                    return -1;
                }
            }
        });

        notifyDataSetChanged();
    }

    public void clear() {
        if (mChatList.size() > 0) {
            mChatList.clear();
        }

        notifyDataSetChanged();
    }
}
