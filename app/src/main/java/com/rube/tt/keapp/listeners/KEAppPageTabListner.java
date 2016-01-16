package com.rube.tt.keapp.listeners;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.rube.tt.keapp.KEApp;

/**
 * Created by rube on 5/7/15.
 */
public class KEAppPageTabListner implements ViewPager.OnPageChangeListener {

    private KEApp keApp;

    public KEAppPageTabListner(KEApp activity){
        this.keApp = activity;
    }

     // This method will be invoked when a new page becomes selected.
    @Override
    public void onPageSelected(int position) {
        //Change switched position and then force  a reload of menu options :D wpr
        keApp.selectedTab = position;
        keApp.supportInvalidateOptionsMenu();

    }

    // This method will be invoked when the current page is scrolled
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Code goes here
    }

    // Called when the scroll state changes:
    // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
    @Override
    public void onPageScrollStateChanged(int state) {
            // Code goes here
    }

}
