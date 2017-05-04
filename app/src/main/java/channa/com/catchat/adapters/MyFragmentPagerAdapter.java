package channa.com.catchat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import channa.com.catchat.fragments.AccountTab;
import channa.com.catchat.fragments.ChatsTab;
import channa.com.catchat.fragments.FriendsTab;

/**
 * Created by Nancy on 1/19/2017.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final static String TAG = "MyFragmentPagerAdapter";
    public static final int FRIENDS_TAB = 0;
    public static final int CHATS_TAB = 1;
    public static final int ACCOUNT_TAB = 2;

    private final int mNumOfTabs = 3;
    private Context mContext;
    private FriendsTab mFriendsTab;
    private ChatsTab mChatsTab;
    private AccountTab mAccountTab;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.mContext = context;
        this.mFriendsTab = new FriendsTab();
        this.mChatsTab = new ChatsTab();
        this.mAccountTab = new AccountTab();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRIENDS_TAB:
                return this.mFriendsTab;
            case CHATS_TAB:
                return this.mChatsTab;
            case ACCOUNT_TAB:
                return this.mAccountTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
