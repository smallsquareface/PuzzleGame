package com.qinkai.puzzlegame.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class PuzzleGame extends Activity {

    public static final String SELECTED_RES_ID = "SelectedID";
    public static final String PIC_PATH = "PicPath";
    public static int DIFFICULT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap selectBmp;

        Intent intent = getIntent();
        int selectResId = intent.getIntExtra(SELECTED_RES_ID, 0);
        if (selectResId != 0) {
            selectBmp = BitmapFactory.decodeResource(getResources(), selectResId);
        } else {
            String path = intent.getStringExtra(PIC_PATH);
            selectBmp = BitmapFactory.decodeFile(path);
        }

    }
}
