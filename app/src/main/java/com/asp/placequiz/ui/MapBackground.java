package com.asp.placequiz.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

public class MapBackground {

    public static class BitmapManager {
        private static final String TAG = "MapBackgroundManager";
        private static BitmapManager instance;

        private Bitmap mMapBitmap;

        private BitmapManager() { }

        public Bitmap getMapBitmap(Context context) {
            if (mMapBitmap == null) {
                try {
                    AssetManager assets = context.getAssets();
                    String[] backgroundFileNameList = assets.list("backgrounds");
                    Bitmap map = null;
                    Point size = getDisplaySize(context);
                    try {
                        map = BitmapFactory.decodeStream(assets.open("backgrounds/" + backgroundFileNameList[0]));
                        float widthRatio = ((float) size.x / (float) map.getWidth()) + 1.0f;
                        float heightRatio = ((float) size.y / (float) map.getHeight()) + 1.0f;
                        float ratio = Math.max(widthRatio, heightRatio);
                        mMapBitmap = Bitmap.createScaledBitmap(map,
                                (int) (map.getWidth() * ratio),
                                (int) (map.getHeight() * ratio), false);
                    } finally {
                        if (map != null) {
                            map.recycle();
                            map = null;
                        }
                    }
                } catch (IOException ex) {
                    Log.e(TAG, "Error trying to locate backgrounds asset directory for " +
                            "background assets");
                    ex.printStackTrace();
                }
            }
            return mMapBitmap;
        }

        private Point getDisplaySize(Context context) {
            Point size = new Point();
            size.x = context.getResources().getDisplayMetrics().widthPixels;
            size.y = context.getResources().getDisplayMetrics().heightPixels;
            return size;
        }

        public void cleanup() {
            if (mMapBitmap != null) {
                mMapBitmap.recycle();
                mMapBitmap = null;
            }
        }

        public static BitmapManager getInstance() {
            if (instance == null) {
                instance = new BitmapManager();
            }
            return instance;
        }

    }

    private ImageView image;
    private float x;
    private float y;

    public MapBackground(ImageView image) {
        this.image = image;
    }

    // Called by ObjectAnimator
    public void setTranslationX(float x) {
        this.x = x;
        calculateMatrix();
    }

    // Called by ObjectAnimator
    public void setTranslationY(float y) {
        this.y = y;
        calculateMatrix();
    }

    private void calculateMatrix() {
        Matrix matrix = new Matrix();
        matrix.setTranslate(-x, -y);
        image.setImageMatrix(matrix);
    }
}