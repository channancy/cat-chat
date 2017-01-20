package channa.com.catchat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import channa.com.catchat.R;
import channa.com.catchat.fragments.ChatsTab;
import channa.com.catchat.fragments.FriendsTab;

/**
 * Created by Nancy on 1/19/2017.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    public final static String TAG = "MyFragmentPagerAdapter";

    private final int mNumOfTabs = 2;
    private Context mContext;
    private FriendsTab mFriendsTab;
    private ChatsTab mChatsTab;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.mContext = context;
        this.mFriendsTab = new FriendsTab();
        this.mChatsTab = new ChatsTab();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return this.mFriendsTab;
            case 1:
                return this.mChatsTab;
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
            default:
                return null;
        }
    }
}
