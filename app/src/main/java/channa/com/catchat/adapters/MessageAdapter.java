package channa.com.catchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import channa.com.catchat.R;
import channa.com.catchat.models.Message;

import static channa.com.catchat.models.Message.INCOMING;
import static channa.com.catchat.models.Message.OUTGOING;

/**
 * Created by Nancy on 4/4/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "MessageAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Message> mMessages;

    public MessageAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case INCOMING:
                view = mLayoutInflater.inflate(R.layout.item_incoming_message, parent, false);
                return new MyMessageHolder(view);

            case OUTGOING:
                view = mLayoutInflater.inflate(R.layout.item_outgoing_message, parent, false);
                return new FriendMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        return message.getType();
    }

    public class MyMessageHolder extends RecyclerView.ViewHolder {
        ImageView friendAvatar;
        TextView friendName;

        public MyMessageHolder (View itemView) {
            super(itemView);

            friendAvatar = (ImageView) itemView.findViewById(R.id.iv_friend_avatar);
            friendName = (TextView) itemView.findViewById(R.id.tv_friend_name);
        }
    }

    public class FriendMessageHolder extends RecyclerView.ViewHolder {
        ImageView friendAvatar;
        TextView friendName;

        public FriendMessageHolder (View itemView) {
            super(itemView);

            friendAvatar = (ImageView) itemView.findViewById(R.id.iv_friend_avatar);
            friendName = (TextView) itemView.findViewById(R.id.tv_friend_name);
        }
    }
}
