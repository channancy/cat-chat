package channa.com.catchat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import channa.com.catchat.R;
import channa.com.catchat.adapters.MyFragmentPagerAdapter;
import channa.com.catchat.models.User;

/**
 * https://www.udacity.com/course/firebase-in-a-weekend-by-google-android--ud0352
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Arbitrary request code values
    public static final int RC_SIGN_IN = 1;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    private String mUsername;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up TabLayout with ViewPager
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch(tab.getPosition()) {
                    case MyFragmentPagerAdapter.FRIENDS_TAB:
                        setTitle(R.string.friends);
                        break;
                    case MyFragmentPagerAdapter.CHATS_TAB:
                        setTitle(R.string.chats);
                        break;
                    case MyFragmentPagerAdapter.ACCOUNT_TAB:
                        setTitle(R.string.account);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Set tab icons
        TabLayout.Tab tabCall = tabLayout.getTabAt(MyFragmentPagerAdapter.FRIENDS_TAB);
        tabCall.setIcon(R.drawable.selector_friends);
        tabCall = tabLayout.getTabAt(MyFragmentPagerAdapter.CHATS_TAB);
        tabCall.setIcon(R.drawable.selector_chats);
        tabCall = tabLayout.getTabAt(MyFragmentPagerAdapter.ACCOUNT_TAB);
        tabCall.setIcon(R.drawable.selector_account);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        // Check for user
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                // Signed in
                if (user != null) {
                    onSignedInInitialize(user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged: " + user.getEmail());

                    // Check if user is in database
                    mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Existing user
                            if (dataSnapshot.hasChild(user.getUid())) {
                                Log.d(TAG, "existing user");

                                /* Workaround for Firebase bug:
                                   Users signing in with email will be able to save display name the second time they log in
                                 */
                                mUsersDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User appUser = dataSnapshot.getValue(User.class);
                                        if (appUser.getName() == null) {
                                            User saveUser = new User(user.getUid(), user.getDisplayName(), user.getEmail(), null);
                                            mUsersDatabaseReference.child(user.getUid()).setValue(saveUser);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            // New user
                            else {
                                User saveUser = new User(user.getUid(), user.getDisplayName(), user.getEmail(), null);
                                mUsersDatabaseReference.child(user.getUid()).setValue(saveUser);
                                Log.d(TAG, "new user");

                                /* Workaround for Firebase bug:
                                   Users signing in with email need to log out and log back in for the display name
                                 */
                                if (saveUser.getName() == null) {
                                    AuthUI.getInstance().signOut(MainActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                // Signed out
                else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.cat_silhouette)
                                    // SmartLock saves user's credentials and tries to log them in
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Sign in
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        Log.d(TAG, "onSignedInInitialize: " + mUsername);
    }

    private void onSignedOutCleanup() {
        // Unset username
        mUsername = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detach listener
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Attach listener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend_menu:
                // Add friend
                Intent intent = new Intent(this, AddFriendActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }
}
