package channa.com.catchat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import channa.com.catchat.R;
import channa.com.catchat.fragments.AccountTab;
import channa.com.catchat.fragments.ChatsTab;
import channa.com.catchat.fragments.FriendsTab;

/**
 * Created by Nancy on 1/19/2017.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final static String TAG = "MyFragmentPagerAdapter";

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
            case 0:
                return this.mFriendsTab;
            case 1:
                return this.mChatsTab;
            case 2:
                return this.mAccountTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.friends);
            case 1:
                return mContext.getString(R.string.chats);
            case 2:
                return mContext.getString(R.string.account);
            default:
                return null;
        }
    }
}
