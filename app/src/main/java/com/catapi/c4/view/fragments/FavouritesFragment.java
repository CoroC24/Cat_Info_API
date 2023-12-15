package com.catapi.c4.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.catapi.c4.R;

public class FavouritesFragment extends Fragment {

    public FavouritesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View viewLayout = inflater.inflate(R.layout.fragment_favourites, container, false);

        return viewLayout;
    }
}
