package com.dbhackathon.ui.poi;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Poi;
import com.dbhackathon.ui.BaseActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

public class PoiActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Poi mPoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_poi);

        ButterKnife.bind(this);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap);
        supportMapFragment.getMapAsync(this);

        if (savedInstanceState == null) {
            Timber.e("POI arg was missing!");
            finish();
            return;
        }

        mPoi = savedInstanceState.getParcelable(Config.EXTRA_POI);

        if (mPoi == null) {
            Timber.e("POI arg was missing!");
            finish();
        }
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coffee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                finish();
                return true;
        }

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(mPoi.lat()), Double.parseDouble(mPoi.lng())))
                .title(mPoi.address())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
}
