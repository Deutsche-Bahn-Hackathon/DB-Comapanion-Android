package com.dbhackathon.ui.coffee;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dbhackathon.R;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

public class CoffeeActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coffee);
        ButterKnife.bind(this);
    }
}
