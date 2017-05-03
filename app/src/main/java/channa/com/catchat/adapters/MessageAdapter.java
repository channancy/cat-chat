package channa.com.catchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import channa.com.catchat.R;
import channa.com.catchat.models.Message;
import channa.com.catchat.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nancy on 4/4/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "MessageAdapter";

    public final static int MY_MESSAGE = 0;
    public final static int FRIEND_MESSAGE = 1;

    private FirebaseDatabase mFirebaseDatabase;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String mUserID;
    private String mUserAvatarUrl;
    private List<Message> mMessages = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat;
    private TimeZone mTimeZone;

    public MessageAdapter(Context context, String userID) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mUserID = userID;

        mSimpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        mTimeZone = TimeZone.getDefault();
        mSimpleDateFormat.setTimeZone(this.mTimeZone);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Message message = mMessages.get(position);
        boolean isPhoto = message.getPhotoUrl() != null;

        // My messages
        if (mUserID.equals(message.getUserID())) {
            // Photo message
            if (isPhoto) {
                ((MyMessageHolder) holder).myMessage.setVisibility(View.GONE);
                ((MyMessageHolder) holder).myPhoto.setVisibility(View.VISIBLE);
                Glide.with(((MyMessageHolder) holder).myPhoto.getContext())
                        .load(message.getPhotoUrl())
                        .fitCenter()
                        .into(((MyMessageHolder) holder).myPhoto);
            }
            // Text message
            else {
                ((MyMessageHolder) holder).myPhoto.setVisibility(View.GONE);
                ((MyMessageHolder) holder).myMessage.setVisibility(View.VISIBLE);
                ((MyMessageHolder) holder).myMessage.setText(message.getText());
            }

            // Timestamp
            Date date = new Date(message.getDateCreatedLong());
            String formattedDate = mSimpleDateFormat.format(date);
            ((MyMessageHolder) holder).myMessageTimestamp.setText(formattedDate);

//            Log.d(TAG, "Locale.getDefault() " + Locale.getDefault());
//            Log.d(TAG, "mTimeZone: " + mTimeZone);
        }

        // Friends' messages
        else {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.getReference().child("users").child(message.getUserID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    // Use uploaded profile picture
                    if (user.getAvatarUrl() != null) {
                        mUserAvatarUrl = user.getAvatarUrl();

                    }
                    // Otherwise, use default profile picture
                    else {
                        mUserAvatarUrl = "http://goo.gl/gEgYUd";
                    }

                    Glide.with(((FriendMessageHolder) holder).friendAvatar.getContext())
                            .load(mUserAvatarUrl)
                            .fitCenter()
                            .into(((FriendMessageHolder) holder).friendAvatar);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Photo message
            if (isPhoto) {
                ((FriendMessageHolder) holder).friendMessage.setVisibility(View.GONE);
                ((FriendMessageHolder) holder).friendPhoto.setVisibility(View.VISIBLE);
                Glide.with(((FriendMessageHolder) holder).friendPhoto.getContext())
                        .load(message.getPhotoUrl())
                        .fitCenter()
                        .into(((FriendMessageHolder) holder).friendPhoto);
            }
            // Text message
            else {
                ((FriendMessageHolder) holder).friendPhoto.setVisibility(View.GONE);
                ((FriendMessageHolder) holder).friendMessage.setVisibility(View.VISIBLE);
                ((FriendMessageHolder) holder).friendMessage.setText(message.getText());
            }

            // Timestamp
            Date date = new Date(message.getDateCreatedLong());
            String formattedDate = mSimpleDateFormat.format(date);
            ((FriendMessageHolder) holder).friendMessageTimestamp.setText(formattedDate);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);

        if (mUserID.equals(message.getUserID())) {
            return MY_MESSAGE;
        }

        return FRIEND_MESSAGE;
    }

    public class MyMessageHolder extends RecyclerView.ViewHolder {
        LinearLayout myMessageContainer;
        TextView myMessage;
        ImageView myPhoto;
        TextView myMessageTimestamp;

        public MyMessageHolder(View itemView) {
            super(itemView);

            myMessageContainer = (LinearLayout) itemView.findViewById(R.id.ll_my_message_container);
            myMessage = (TextView) itemView.findViewById(R.id.tv_my_message);
            myPhoto = (ImageView) itemView.findViewById(R.id.iv_my_photo);
            myMessageTimestamp = (TextView) itemView.findViewById(R.id.tv_my_message_timestamp);
        }
    }

    public class FriendMessageHolder extends RecyclerView.ViewHolder {
        LinearLayout friendMessageContainer;
        CircleImageView friendAvatar;
        TextView friendMessage;
        ImageView friendPhoto;
        TextView friendMessageTimestamp;

        public FriendMessageHolder(View itemView) {
            super(itemView);

            friendMessageContainer = (LinearLayout) itemView.findViewById(R.id.ll_friend_message_container);
            friendAvatar = (CircleImageView) itemView.findViewById(R.id.iv_friend_avatar_message);
            friendMessage = (TextView) itemView.findViewById(R.id.tv_friend_message);
            friendPhoto = (ImageView) itemView.findViewById(R.id.iv_friend_photo);
            friendMessageTimestamp = (TextView) itemView.findViewById(R.id.tv_friend_message_timestamp);
        }
    }

    public void add(int position, Message message) {
        mMessages.add(position, message);
        notifyItemInserted(position);
    }

    public void clear() {
        if (mMessages.size() > 0) {
            mMessages.clear();
        }

        notifyDataSetChanged();
    }
}
