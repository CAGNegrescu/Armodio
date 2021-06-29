package com.example.gymapp.Dietdays;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.gymapp.Dietdays.Friday;
import com.example.gymapp.Dietdays.Monday;
import com.example.gymapp.Dietdays.Saturday;
import com.example.gymapp.Dietdays.Sunday;
import com.example.gymapp.Dietdays.Thursday;
import com.example.gymapp.Dietdays.Tuesday;
import com.example.gymapp.Dietdays.Wednesday;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int tabNo;

    public ViewPagerAdapter(@NonNull FragmentManager fm,int tabNo) {
        super(fm);
        this.tabNo=tabNo;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:

                return new Monday();
            case 1:

                return new Tuesday();
            case 2:

                return new Wednesday();
            case 3:

                return new Thursday();
            case 4:

                return new Friday();
            case 5:

                return new Saturday();
            case 6:

                return new Sunday();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNo;
    }
}
