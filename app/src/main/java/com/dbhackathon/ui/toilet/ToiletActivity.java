package com.dbhackathon.ui.toilet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Facility;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.data.network.NetUtils;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.network.api.FacilityApi;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.widget.RecyclerItemDivider;
import com.dbhackathon.util.NextObserver;
import com.dbhackathon.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Allows the user to pick a departure/arrival bus stop by searching it by either title or
 * municipality. When starting this activity it shows a nice reveal animation.
 *
 * @author Alex Lardschneider
 */
public class ToiletActivity extends BaseActivity {

    @BindView(R.id.refresh) SwipeRefreshLayout mRefresh;

    public static final String EXTRA_X_POS = "com.dbhackathon.EXTRA_X_POS";
    public static final String EXTRA_Y_POS = "com.dbhackathon.EXTRA_Y_POS";

    private int mSearchX;
    private int mSearchY;

    private List<Facility> mItems;
    private ToiletAdapter mAdapter;

    private Train mTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (!intent.hasExtra(Config.EXTRA_TRAIN)) {
            Timber.e("Missing intent extra %s", Config.EXTRA_TRAIN);
            finish();
            return;
        }

        mTrain = intent.getParcelableExtra(Config.EXTRA_TRAIN);

        setResult(RESULT_CANCELED, getIntent());

        setContentView(R.layout.activity_toilet);
        ButterKnife.bind(this);

        mSearchX = (int) intent.getFloatExtra(EXTRA_X_POS, 0);
        mSearchY = (int) intent.getFloatExtra(EXTRA_Y_POS, 0);

        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);
        mRefresh.setRefreshing(true);
        mRefresh.setOnRefreshListener(this::parseData);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = getToolbar();
        toolbar.setNavigationOnClickListener(view -> navigateUpOrBack(this));

        doEnterAnim();

        overridePendingTransition(0, 0);

        mItems = new ArrayList<>();
        mAdapter = new ToiletAdapter(this, mItems);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new RecyclerItemDivider(this));
        recyclerView.setAdapter(mAdapter);

        parseData();
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_INVALID;
    }


    private void parseData() {
        if (!NetUtils.isOnline(this)) {
            Timber.e("Device is OFFLINE");
            return;
        }

        FacilityApi facilityApi = RestClient.ADAPTER.create(FacilityApi.class);
        facilityApi.toilets(mTrain.name().replace(" ", "").toLowerCase(), "22")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextObserver<List<Facility>>() {
                    @Override
                    public void onNext(List<Facility> facilities) {
                        mItems.clear();
                        mItems.addAll(facilities);

                        mAdapter.notifyDataSetChanged();

                        mRefresh.setRefreshing(false);
                    }
                });
    }

    /**
     * On Lollipop+ perform a circular reveal animation (an expanding circular mask) when showing
     * the search panel.
     */
    private void doEnterAnim() {
        // Fade in a background scrim as this is a floating window. We could have used a
        // translucent window background but this approach allows us to turn off window animation &
        // overlap the fade with the reveal animation – making it feel snappier.
        View scrim = findViewById(R.id.scrim);
        scrim.animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();

        // Next perform the circular reveal on the search panel
        View searchPanel = findViewById(R.id.search_panel);
        if (searchPanel != null) {
            // We use a view tree observer to set this up once the view is measured & laid out
            searchPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    searchPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                    // As the height will change once the initial suggestions are delivered by the
                    // loader, we can't use the search panels height to calculate the final radius
                    // so we fall back to it's parent to be safe

                    int[] positions = new int[2];
                    searchPanel.getLocationOnScreen(positions);

                    int x = mSearchX == 0 ? searchPanel.getTop() : mSearchX;
                    int y = mSearchY == 0 ? (searchPanel.getLeft() + searchPanel.getRight()) / 2 : mSearchY;

                    x -= positions[0];
                    y -= positions[1];

                    View parent = (View) searchPanel.getParent();

                    int radius = (int) Math.sqrt(Math.pow(parent.getHeight(), 2) +
                            Math.pow(parent.getWidth(), 2));

                    // Center the animation on the top right of the panel i.e. near to the
                    // search button which launched this screen.
                    Animator show = ViewAnimationUtils.createCircularReveal(searchPanel,
                            x, y, 0f, radius);

                    show.setDuration(750);
                    show.setInterpolator(new FastOutSlowInInterpolator());
                    show.start();

                    return false;
                }
            });
        }
    }

    /**
     * On Lollipop+ perform a circular animation (a contracting circular mask) when hiding the
     * search panel.
     */
    private void doExitAnim() {
        View searchPanel = findViewById(R.id.search_panel);
        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left

        int[] positions = new int[2];
        searchPanel.getLocationOnScreen(positions);

        int x = mSearchX == 0 ? searchPanel.getTop() : mSearchX;
        int y = mSearchY == 0 ? (searchPanel.getLeft() + searchPanel.getRight()) / 2 : mSearchY;

        x -= positions[0];
        y -= positions[1];

        int radius = (int) Math.sqrt(Math.pow(searchPanel.getWidth(), 2)
                + Math.pow(searchPanel.getHeight(), 2));

        // Animating the radius to 0 produces the contracting effect
        Animator shrink = ViewAnimationUtils.createCircularReveal(searchPanel, x, y, radius, 0f);

        shrink.setDuration(400);
        shrink.setInterpolator(new FastOutSlowInInterpolator());
        shrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                searchPanel.setVisibility(View.INVISIBLE);
                ActivityCompat.finishAfterTransition(ToiletActivity.this);
            }
        });
        shrink.start();

        // We also animate out the translucent background at the same time.
        findViewById(R.id.scrim).animate()
                .alpha(0f)
                .setDuration(400)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    public void dismiss(View view) {
        UIUtils.hideKeyboard(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doExitAnim();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }
}
