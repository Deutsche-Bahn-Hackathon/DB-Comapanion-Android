package com.dbhackathon.ui.coffee;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Coffee;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CoffeeActivity extends RxAppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.coffee_description_text) TextView mDescriptionText;
    @BindView(R.id.coffee_seat_number) EditText mSeatNumber;
    @BindView(R.id.coffee_confirm_button) Button mButton;
    @BindView(R.id.coffee_spinner) Spinner mSpinner;

    private Coffee[] mCoffees;
    private Coffee mSelectedCoffee;

    NumberFormat format = NumberFormat.getCurrencyInstance();

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

        Parcelable[] parcelables = intent.getParcelableArrayExtra(Config.EXTRA_COFFEE);
        mCoffees = new Coffee[parcelables.length];

        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(parcelables, 0, mCoffees, 0, parcelables.length);

        setupSpinner();

        mDescriptionText.setText(String.format(Locale.getDefault(), "Dou you want to order a %s for %s? A servant will pass by soon and distribute it.",  mCoffees[0].name, format.format(mSelectedCoffee.price)));
        mButton.setText(String.format(Locale.getDefault(), "Order %s", format.format(mCoffees[0].price)));

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
                if (mSelectedCoffee != null) {
                    Toast.makeText(getApplicationContext(), "Thank you for your order!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a coffee!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void setupSpinner() {
        List<String> selectableItems = new ArrayList<>();

        for (Coffee coffee : mCoffees) {
            selectableItems.add(coffee.name);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, selectableItems);

        mSpinner.setAdapter(dataAdapter);

        mSpinner.setOnItemSelectedListener(this);

        mSelectedCoffee = mCoffees[0];
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedCoffee = mCoffees[position];

        mDescriptionText.setText(String.format(Locale.getDefault(), "Dou you want to order a %s for %s? A servant will pass by soon and distribute it.",  mSelectedCoffee.name, format.format(mSelectedCoffee.price)));
        mButton.setText(String.format(Locale.getDefault(), "Order %s", format.format(mSelectedCoffee.price)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
