package in.krharsh17.programmersdate.home.bottompager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class BottomPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<DetailFragment> fragments;

    public BottomPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
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
