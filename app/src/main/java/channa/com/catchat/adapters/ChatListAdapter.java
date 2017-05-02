package channa.com.catchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nancy on 4/26/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private final static String TAG = "ChatListAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Chat> mChatList = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat;
    private TimeZone mTimeZone;
    private String mUserAvatarUrl;

    public ChatListAdapter(Context context, String userAvatarUrl) {
        mContext = context;
        mUserAvatarUrl = userAvatarUrl;
        mLayoutInflater = LayoutInflater.from(context);

        mSimpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        mTimeZone = TimeZone.getDefault();
        mSimpleDateFormat.setTimeZone(this.mTimeZone);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_chat, parent, false);
        ChatListAdapter.ViewHolder holder = new ChatListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mChatList.get(position);
        holder.title.setText(chat.getTitle());
        holder.lastMessage.setText(chat.getLastMessage());

        Date date = new Date(chat.getDateCreatedLong());
        String formattedDate = mSimpleDateFormat.format(date);
        holder.lastTimestamp.setText(formattedDate);

        if (chat.getAvatarUrl() != null) {
            Glide.with(mContext).load(chat.getAvatarUrl()).into(holder.avatarUrl);
        }
        else {
            Glide.with(mContext).load("http://goo.gl/gEgYUd").into(holder.avatarUrl);
        }
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
            args.putString("chatID", chat.getMessageID());
            args.putString("userAvatarUrl", mUserAvatarUrl);
            args.putString("friendAvatarUrl", chat.getAvatarUrl());
            args.putString("friendName", chat.getTitle());
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
                Log.d(TAG, "compare: ");
                
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
