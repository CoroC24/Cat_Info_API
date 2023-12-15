package com.catapi.c4.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.catapi.c4.R;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.fragments.AccountFragment;
import com.catapi.c4.view.fragments.FavouritesFragment;
import com.catapi.c4.view.fragments.MainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Fragment fragmentVisible;
    private int animIn, animOut;
    private final Fragment[] fragments = new Fragment[]{new MainFragment(), new FavouritesFragment(), new AccountFragment()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkOverlayPermission();

        Utils.context = getApplicationContext();
        NavigationView navigationView = findViewById(R.id.navigationView);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragments[0])
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[1], "favouritesFragment").hide(fragments[1])
                .add(R.id.fragment_container, fragments[2], "accountFragment").hide(fragments[2])
                .commit();

        fragmentVisible = fragments[0];

        bottomNavigationView.setSelectedItemId(R.id.navCat);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.navCat) {
                fragment = fragments[0];
            } else if (item.getItemId() == R.id.navFavourites) {
                fragment = fragments[1];
            } else if (item.getItemId() == R.id.navAccount) {
                fragment = fragments[2];
            }

            if (fragment != null) {
                if (fragmentVisible instanceof MainFragment) {
                    animIn = R.anim.slide_in_right;
                    animOut = R.anim.slide_out_left;
                } else if (fragmentVisible instanceof AccountFragment) {
                    animIn = android.R.anim.slide_in_left;
                    animOut = android.R.anim.slide_out_right;
                } else if (fragmentVisible instanceof FavouritesFragment && fragment instanceof MainFragment) {
                    animIn = android.R.anim.slide_in_left;
                    animOut = android.R.anim.slide_out_right;
                } else if (fragmentVisible instanceof FavouritesFragment && fragment instanceof AccountFragment) {
                    animIn = R.anim.slide_in_right;
                    animOut = R.anim.slide_out_left;
                }

                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .setCustomAnimations(animIn, animOut)
                        .hide(fragments[0])
                        .hide(fragments[1])
                        .hide(fragments[2])
                        .show(fragment)
                        .commit();

                fragmentVisible = fragment;
            }

            return true;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.close();
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "PERMISO SUPERPOSICIÓN ACTIVADO", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "PERMISO SUPERPOSICIÓN INACTIVO", Toast.LENGTH_LONG).show();
                checkOverlayPermission();
            }
        }
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1234);
        }
    }
}