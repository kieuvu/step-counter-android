package com.vukm.StepCounter;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.vukm.StepCounter.ui.counting.CountingTabFragment;
import com.vukm.StepCounter.ui.summary.SummaryTabFragment;

import org.jetbrains.annotations.Contract;

public class MainActivity extends AppCompatActivity {
    private Fragment countingTabFragment;
    private Fragment summaryTabFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        this.countingTabFragment = new CountingTabFragment();
        this.summaryTabFragment = new SummaryTabFragment();
        this.activeFragment = this.countingTabFragment;

        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, this.summaryTabFragment, "SUMMARY")
                .hide(this.summaryTabFragment)
                .commit();

        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, this.countingTabFragment, "COUNTING")
                .commit();

        BottomNavigationView bottomNavigationView = this.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this.onItemSelectedListener());
    }

    private void showFragment(Fragment fragment) {
        if (fragment == this.activeFragment) {
            return;
        }

        this.getSupportFragmentManager().beginTransaction()
                .hide(this.activeFragment)
                .show(fragment)
                .commit();

        this.activeFragment = fragment;
    }

    @NonNull
    @Contract(pure = true)
    private NavigationBarView.OnItemSelectedListener onItemSelectedListener() {
        return (MenuItem item) -> {
            if (item.getItemId() == R.id.nav_tracking) {
                this.showFragment(this.countingTabFragment);
            } else if (item.getItemId() == R.id.nav_summary) {
                this.showFragment(this.summaryTabFragment);
            }
            return true;
        };
    }
}

