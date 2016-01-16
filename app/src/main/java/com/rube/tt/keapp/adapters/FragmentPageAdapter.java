package com.rube.tt.keapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rube.tt.keapp.fragments.ContactFragment;
import com.rube.tt.keapp.fragments.MessageFragment;
import com.rube.tt.keapp.fragments.PageFragment;

/**
 * Created by rube on 4/1/15.
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[] { "Chats", "Contacts", "Pages" };

        public FragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment =  new MessageFragment();
                    break;
                case 1:
                    fragment = new ContactFragment();
                    break;
                case 2:
                    fragment =  new PageFragment();
                    break;
                default:
                    fragment =  new MessageFragment();
                    break;

            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

