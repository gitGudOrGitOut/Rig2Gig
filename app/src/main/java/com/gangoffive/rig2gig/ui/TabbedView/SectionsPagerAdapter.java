package com.gangoffive.rig2gig.ui.TabbedView;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gangoffive.rig2gig.TabbedViewReferenceInitialiser;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static int[] TAB_TITLES;
    private static Context mContext = null;
    private static int[] fragments;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int[] tabTitles, int[] fragmentArray) {
        super(fm);
        mContext = context;
        TAB_TITLES = tabTitles;
        fragments = fragmentArray;
    }

    /**
     * Instantiate a fragment
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = PlaceholderFragment.newInstance(position, fragments);
        return fragment;
    }

    /**
     * get tab title for given position
     * @param position of tab
     * @return sequence representing title
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    /**
     * return number of tabs, also used to save data of tabs that may be lost and set the soft
     * input mode of new tabs
     * @return number of tabs
     */
    @Override
    public int getCount() {
        if (TAB_TITLES.length > 2
                && !((TabbedViewReferenceInitialiser) mContext).editingText())
        {
            ((TabbedViewReferenceInitialiser) mContext).saveTabs();
            ((Activity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        else if(TAB_TITLES.length > 2
                && ((TabbedViewReferenceInitialiser) mContext).editingText()
                && !((TabbedViewReferenceInitialiser) mContext).savedOnFocus())
        {
            ((TabbedViewReferenceInitialiser) mContext).saveTabs();
            ((Activity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((TabbedViewReferenceInitialiser) mContext).setSavedOnFocus(true);
        }
        return TAB_TITLES.length;
    }

    /**
     * get context that will hold the tabs
     * @return context
     */
    public static Context getmContext()
    {
        return mContext;
    }
}
