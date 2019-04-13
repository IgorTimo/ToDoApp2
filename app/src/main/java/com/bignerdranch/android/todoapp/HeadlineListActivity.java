package com.bignerdranch.android.todoapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class HeadlineListActivity extends SingleFragmentActivity {



    @Override
    protected Fragment createFragment() {
        return HeadlineListFragment.newInstance();
    }

    // todo упорядочивание на завтра, послезавтра и т.д  предположительно через navigation Drawer
    // todo landscape orientation


}
