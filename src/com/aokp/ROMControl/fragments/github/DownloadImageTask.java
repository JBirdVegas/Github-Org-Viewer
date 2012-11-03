package com.aokp.ROMControl.fragments.github;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final boolean DEBUG = false;
    private final String TAG = getClass().getSimpleName();
    // send String[] (url[0]) return Bitmap
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected void onPreExecute() {
        bmImage.setVisibility(View.GONE);
    }

    protected Bitmap doInBackground(String... urls) {
        String avatarUrl = urls[0];
        if (DEBUG) Log.d(TAG, "downloading: " + avatarUrl);
        Bitmap mAvatar = null;
        try {
            InputStream in = new URL(avatarUrl).openStream();
            mAvatar = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, "failed to download avatar", e);
        }
        return mAvatar;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        bmImage.setVisibility(View.VISIBLE);
    }
}
