package com.dbhackathon.ui.coffee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Coffee;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CoffeeActivity extends RxAppCompatActivity implements View.OnClickListener {

    @BindView(R.id.coffee_description_text) TextView mDescriptionText;
    @BindView(R.id.coffee_seat_number) EditText mSeatNumber;
    @BindView(R.id.coffee_confirm_button) Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coffee);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (!intent.hasExtra(Config.EXTRA_COFFEE)) {
            Timber.e("Missing intent extra %s", Config.EXTRA_COFFEE);
            finish();
            return;
        }

        Coffee coffee = intent.getParcelableExtra(Config.EXTRA_COFFEE);

        String price = NumberFormat.getCurrencyInstance().format(coffee.price);

        mDescriptionText.setText("Dou you want to order a coffee for " + price + "? A servant will pass by soon and distribute it.");
        mButton.setText("Order " + price);

        mButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coffee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coffee_confirm_button:
                Toast.makeText(getApplicationContext(), "Thanks for your order", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }
}
