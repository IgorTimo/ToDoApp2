package com.bignerdranch.android.todoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class ToDoesPagerActivity extends AppCompatActivity {

    private static final String EXTRA_HEADLINE_ID = "todoapp.headline_id";

    private ViewPager mViewPager;
    private List<Headline> mHeadlines;

    public static Intent newIntent(Context context, UUID headlineId) {
        Intent intent = new Intent(context, ToDoesPagerActivity.class);
        intent.putExtra(EXTRA_HEADLINE_ID, headlineId);
        return intent;
    }

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_does_pager);

        UUID headlineId = (UUID) getIntent().getSerializableExtra(EXTRA_HEADLINE_ID);

        mViewPager = findViewById(R.id.to_does_view_pager);

        mHeadlines = HeadlineLab.get(this).getHeadlines();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Headline headline = mHeadlines.get(i);
                return ToDoListFragment.newInstance(headline.getId());
            }

            @Override
            public int getCount() {
                return mHeadlines.size();
            }
        });

        for (int i = 0; i < mHeadlines.size(); i++) {
            if (mHeadlines.get(i).getId().equals(headlineId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
