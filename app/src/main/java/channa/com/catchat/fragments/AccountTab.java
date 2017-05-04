package channa.com.catchat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import channa.com.catchat.R;
import channa.com.catchat.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AccountTab extends Fragment {

    private static final String TAG = "AccountTab";

    // Arbitrary request code values
    private static final int RC_PHOTO_PICKER = 2;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mAvatarPhotosStorageReference;

    private User mUser;
    private String mUserID;

    @BindView(R.id.iv_account_avatar)
    CircleImageView ivAccountAvatar;
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.btn_sign_out)
    Button btnSignOut;

    public AccountTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mAvatarPhotosStorageReference = mFirebaseStorage.getReference().child("avatar_photos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_account_tab, container, false);
        ButterKnife.bind(this, layout);

        Log.d(TAG, "onCreateView: " + mUserID);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // Signed in
                if (user != null) {
                    mUserID = user.getUid();

                    // Retrieve user
                    mUsersDatabaseReference.orderByChild("id").equalTo(mUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot userFound = dataSnapshot.getChildren().iterator().next();
                            mUser = userFound.getValue(User.class);

                            // Load avatar if exists
                            if (mUser.getAvatarUrl() != null) {
                                Glide.with(AccountTab.this).load(mUser.getAvatarUrl()).into(ivAccountAvatar);

                            }
                            // Otherwise load default avatar
                            else {
                                Glide.with(AccountTab.this).load(R.drawable.cat_silhouette_head).into(ivAccountAvatar);
                            }

                            // Set user name
                            tvAccountName.setText(mUser.getName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        // Click profile picture to change
        ivAccountAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Sign out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity());
            }
        });

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Make child named after last path segment of the uri
            // ex) content://local_images/foo/4
            // Child would be named 4
            // Get reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mAvatarPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage, returns an UploadTask
            photoRef.putFile(selectedImageUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: " + mUserID);

                    // Get url from taskSnapshot
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Log.d(TAG, "onSuccess: " + downloadUrl);

                    // Save url to user
                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                    childUpdates.put("avatarUrl", downloadUrl.toString());
                    mUsersDatabaseReference.child(mUserID).updateChildren(childUpdates);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
