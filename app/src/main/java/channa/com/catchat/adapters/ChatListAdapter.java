package channa.com.catchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import channa.com.catchat.R;
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

    public ChatListAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
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
        holder.lastTimestamp.setText(chat.getLastTimestamp());

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

    public class ViewHolder extends RecyclerView.ViewHolder {

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
        }
    }
}
