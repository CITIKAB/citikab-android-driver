package com.trioangle.goferdriver.rating;
/**
 * @package com.trioangle.goferdriver.rating
 * @subpackage rating
 * @category RiderFeedBackModel
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                RiderFeedBackModel
Its used to get the rider feedback details
*************************************************************** */
public class RiderFeedBack extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    public @InjectView(R.id.starcomment)
    RelativeLayout starcomment;
    //This is our tablayout
    public @InjectView(R.id.tabLayout)
    TabLayout tabLayout;
    //This is our viewPager
    public @InjectView(R.id.pager)
    ViewPager viewPager;

    @OnClick(R.id.back_lay)
    public void onpBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        starcomment.setVisibility(View.GONE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        FeedbackViewPagerAdapter adapter = new FeedbackViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Comments(), "COMMENTS");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        CommonMethods.DebuggableLogI("onTabUnselected", "onTabUnselected");
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        CommonMethods.DebuggableLogI("onTabReselected", "onTabReselected");
    }


}
