package com.qinkai.puzzlegame.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.qinkai.puzzlegame.R;
import com.qinkai.puzzlegame.adapter.GridPicListAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PuzzleMain extends Activity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_GALLERY = 1;
    private static final String TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/temp.png";

    private TextView mDifficultyTv;
    private List<Bitmap> mPicList = new ArrayList<>();
    private GridPicListAdapter mPicAdapter;
    private RelativeLayout mMainLayout;
    private ProgressBar mProgressBar;

    private final int[] mResPicId = new int[]{
            R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4,
            R.drawable.pic5, R.drawable.pic6, R.drawable.pic7, R.drawable.pic8,
            R.drawable.pic9, R.drawable.pic10, R.drawable.pic11, R.drawable.pic12,
            R.drawable.pic13, R.drawable.pic14, R.drawable.pic15, R.drawable.plus};

    private static class PuzzleMainHandler extends Handler {
        WeakReference<Context> mContext;

        PuzzleMainHandler(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PuzzleMain activity = (PuzzleMain) mContext.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.showProgressBar(false);
                        activity.mPicAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private PuzzleMainHandler mHandler = new PuzzleMainHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xpuzzle_main);

        mDifficultyTv = findViewById(R.id.tv_difficulty);
        Spinner mDifficultySp = findViewById(R.id.sp_difficulty);
        GridView mPicGridView = findViewById(R.id.gv_pic_list);
        mMainLayout = findViewById(R.id.main_view_layout);
        mProgressBar = findViewById(R.id.main_progressbar);

        mDifficultySp.setSelection(0);
        mDifficultySp.setOnItemSelectedListener(this);

        showProgressBar(true);
        mPicAdapter = new GridPicListAdapter(this, mPicList);
        mPicGridView.setAdapter(mPicAdapter);
        mPicGridView.setOnItemClickListener(this);

        new Thread() {
            @Override
            public void run() {
                InitPicList();
            }
        }.start();
    }

    private void showProgressBar(boolean bShow) {
        if (bShow) {
            mProgressBar.setVisibility(View.VISIBLE);
            mMainLayout.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mMainLayout.setVisibility(View.VISIBLE);
        }
    }

    private void InitPicList() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bitmap[] bitmaps = new Bitmap[mResPicId.length];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), mResPicId[i]);
            mPicList.add(bitmaps[i]);
        }

        mHandler.sendEmptyMessage(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showDifficult();
    }

    private void showDifficult() {
        mDifficultyTv.setText(getString(R.string.difficulty_format, PuzzleGame.DIFFICULT));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_difficulty:
                PuzzleGame.DIFFICULT = position + 2;
                showDifficult();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gv_pic_list:
                if (position == mPicList.size() - 1) {
                    // 选择拍照
                    showCustomPicDialog();
                } else {
                    Intent intent = new Intent(this, PuzzleGame.class);
                    intent.putExtra(PuzzleGame.SELECTED_RES_ID, mResPicId[position]);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }

    }

    private void showCustomPicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose);
        builder.setItems(R.array.cust_pic_dlg_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                    break;

                    case 1: {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, REQUEST_GALLERY);
                    }
                    break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA: {
                    Intent intent = new Intent(this, PuzzleGame.class);
                    intent.putExtra(PuzzleGame.PIC_PATH, TEMP_IMAGE_PATH);
                    startActivity(intent);
                }
                break;

                case REQUEST_GALLERY: {
                    if (data != null && data.getData() != null) {
                        Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int pathData = cursor.getColumnIndex("_data");
                            String path = cursor.getString(pathData);
                            cursor.close();
                            Intent intent = new Intent(this, PuzzleGame.class);
                            intent.putExtra(PuzzleGame.PIC_PATH, path);
                            startActivity(intent);
                        }
                    }
                }
                break;

                default:
                    break;
            }
        }
    }
}
