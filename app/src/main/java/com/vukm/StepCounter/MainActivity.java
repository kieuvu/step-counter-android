package com.vukm.StepCounter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));

        setContentView(R.layout.activity_main);

        countingTabFragment = new CountingTabFragment();
        summaryTabFragment = new SummaryTabFragment();
        activeFragment = countingTabFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, summaryTabFragment, "SUMMARY")
                .hide(summaryTabFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, countingTabFragment, "COUNTING")
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this.onItemSelectedListener());
    }

    private void showFragment(Fragment fragment) {
        if (fragment == activeFragment) return;

        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();

        activeFragment = fragment;
    }

    @NonNull
    @Contract(pure = true)
    private NavigationBarView.OnItemSelectedListener onItemSelectedListener() {
        return (MenuItem item) -> {
            if (item.getItemId() == R.id.nav_tracking) {
               this.showFragment(countingTabFragment);
            } else if (item.getItemId() == R.id.nav_summary) {
                this.showFragment(summaryTabFragment);
            }
            return true;
        };
    }
}

