package com.catapi.c4.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.catapi.c4.R;
import com.catapi.c4.databinding.ActivityMainBinding;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.fragments.AccountFragment;
import com.catapi.c4.view.fragments.FavouritesFragment;
import com.catapi.c4.view.fragments.MainFragment;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private Fragment fragmentVisible;
    private int animIn, animOut;
    private final Fragment[] fragments = new Fragment[]{new MainFragment(), new FavouritesFragment(), new AccountFragment()};
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        checkOverlayPermission();

        mAuth = FirebaseAuth.getInstance();
        Utils.context = getApplicationContext();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragments[0], "mainFragment")
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[1], "favouritesFragment").hide(fragments[1])
                .add(R.id.fragment_container, fragments[2], "accountFragment").hide(fragments[2])
                .commit();

        fragmentVisible = fragments[0];

        setBottomMenuItem(R.id.navCat);

        mainBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switchFragments(item.getItemId());
            return true;
        });

        mainBinding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.favourites_item) {
                switchFragments(item.getItemId());
            } else if (item.getItemId() == R.id.logout_item) {
                mAuth.signOut();
                Utils.loggedUser = null;
            }

            setBottomMenuItem(item.getItemId());
            mainBinding.drawerLayout.close();
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Utils.loggedUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "OVERLAY PERMISSION ENABLED", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "OVERLAY PERMISSION INACTIVE", Toast.LENGTH_LONG).show();
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

    private void switchFragments(int itemId) {
        Fragment fragment = null;

        if (itemId == R.id.navCat) {
            fragment = fragments[0];
        } else if (itemId == R.id.navFavourites || itemId == R.id.favourites_item) {
            fragment = fragments[1];
        } else if (itemId == R.id.navAccount) {
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
    }

    private void setBottomMenuItem(int itemId) {
        mainBinding.bottomNavigation.setSelectedItemId(itemId);
    }
}