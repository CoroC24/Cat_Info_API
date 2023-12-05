package com.catapi.c4.model;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.catapi.c4.R;
import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.data.ListCatResponse;

public class Utils {

    private static WindowManager windowManager;
    private static View myView;

    public static InfoCatResponse infoCatResponse;

    public static void createDialog(Context context, ListCatResponse dataResponse) {
        Breeds breedsData = new Breeds();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            params.flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN;
        }

        params.width = 1080;
        params.height = 1920;

        windowManager = (WindowManager) context.getApplicationContext().getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = inflater.inflate(R.layout.cat_description, null);
        windowManager.addView(myView, params);
        TextView catName = myView.findViewById(R.id.tvCatName);
        TextView description = myView.findViewById(R.id.tvDescription);
        ImageView image = myView.findViewById(R.id.imgCat);

        catName.setText(breedsData.getName());
        description.setText(breedsData.getDescription());
//        Glide.with(context).load(dataResponse.getUrl()).into(image);

        myView.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
                windowManager.removeViewImmediate(myView);
            }
            return false;
        });
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                    return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                }
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }

        return false;
    }
}
