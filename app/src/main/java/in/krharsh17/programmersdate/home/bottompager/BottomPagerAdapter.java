package in.krharsh17.programmersdate.home.bottompager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import in.krharsh17.programmersdate.Constants;

public class BottomPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<DetailFragment> fragments;

    public BottomPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new DetailFragment().setTaskType(Constants.taskTypeTwister, 1));
    }

    public ArrayList<DetailFragment> getFragments() {
        return fragments;
    }

    public void setFragments(ArrayList<DetailFragment> fragments) {
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

}
