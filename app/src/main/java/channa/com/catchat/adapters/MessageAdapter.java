package channa.com.catchat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import channa.com.catchat.R;
import channa.com.catchat.models.Message;

/**
 * Created by Nancy on 4/4/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "MessageAdapter";

    public final static int MY_MESSAGE = 0;
    public final static int FRIEND_MESSAGE = 1;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String userID;
    private List<Message> mMessages;

    public MessageAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // Signed in
                if (user != null) {
                    userID = user.getUid();
                }
                // Signed out
                else {
                    userID = null;
                }
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case MY_MESSAGE:
                view = mLayoutInflater.inflate(R.layout.item_my_message, parent, false);
                return new MyMessageHolder(view);

            case FRIEND_MESSAGE:
                view = mLayoutInflater.inflate(R.layout.item_friend_message, parent, false);
                return new FriendMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        if (userID.equals(message.getUserID())) {
            ((MyMessageHolder) holder).myMessage.setText(message.getText());
        }
        else {
            ((FriendMessageHolder) holder).friendMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);

        if (userID.equals(message.getUserID())) {
            return MY_MESSAGE;
        }

        return FRIEND_MESSAGE;
    }

    public class MyMessageHolder extends RecyclerView.ViewHolder {
        TextView myMessage;

        public MyMessageHolder (View itemView) {
            super(itemView);

            myMessage = (TextView) itemView.findViewById(R.id.tv_friend_message);
        }
    }

    public class FriendMessageHolder extends RecyclerView.ViewHolder {
        ImageView friendAvatar;
        TextView friendMessage;

        public FriendMessageHolder (View itemView) {
            super(itemView);

            friendAvatar = (ImageView) itemView.findViewById(R.id.iv_friend_avatar_message);
            friendMessage = (TextView) itemView.findViewById(R.id.tv_friend_message);
        }
    }

    public void setMessageList(List<Message> messages) {
        mMessages = messages;
        notifyDataSetChanged();
    }

    public void clear() {
        if (mMessages != null) {
            mMessages.clear();
        }

        notifyDataSetChanged();
    }
}
