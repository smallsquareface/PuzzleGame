package com.qinkai.puzzlegame.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinkai.puzzlegame.R;
import com.qinkai.puzzlegame.adapter.GridItemsAdapter;
import com.qinkai.puzzlegame.bean.ItemBean;
import com.qinkai.puzzlegame.util.GameUtil;
import com.qinkai.puzzlegame.util.ImagesUtil;
import com.qinkai.puzzlegame.util.ScreenUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PuzzleGame extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String SELECTED_RES_ID = "SelectedID";
    public static final String PIC_PATH = "PicPath";
    public static int DIFFICULT = 2;
    public static Bitmap mLastBitmap;
    private TextView mStepContent;
    private TextView mTimeContent;
    private GridView mGameGridView;
    private ImageView mOrigImageView;
    private List<Bitmap> mBlockItemList = new ArrayList<>();
    private Bitmap mOriginalBmp;
    private boolean mIsShowImg = false;
    private GridItemsAdapter mGameAdapter;


    private static class PuzzleGameHandler extends Handler {
        WeakReference<Context> mContext;

        PuzzleGameHandler(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PuzzleGame activity = (PuzzleGame) mContext.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.mHandler.sendEmptyMessageDelayed(1, 1000);
                        break;

                    case 2:
                        activity.mGameAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private PuzzleGameHandler mHandler = new PuzzleGameHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xpuzzle_game);

        mStepContent = findViewById(R.id.tv_step_content);
        mTimeContent = findViewById(R.id.tv_time_content);
        Button mOrigPicBtn = findViewById(R.id.btn_orig_pic);
        Button mResetBtn = findViewById(R.id.btn_reset);
        Button mBackBtn = findViewById(R.id.btn_back);
        mOrigPicBtn.setOnClickListener(this);
        mResetBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mGameGridView = findViewById(R.id.gv_puzzle_game);
        mOrigImageView = findViewById(R.id.img_orig_pic);
        mOrigImageView.setVisibility(View.GONE);

        // Flag 是否已显示原图
        mIsShowImg = false;

        Intent intent = getIntent();

        // 获取选择的图片, 选择默认图片还是自定义图片
        int selectResId = intent.getIntExtra(SELECTED_RES_ID, 0);
        Bitmap originBmp;
        if (selectResId != 0) {
            originBmp = BitmapFactory.decodeResource(getResources(), selectResId);
        } else {
            String path = intent.getStringExtra(PIC_PATH);
            originBmp = BitmapFactory.decodeFile(path);
        }

        if (originBmp == null) {
            Log.e("Jerry Qin", "originBmp == null");
            return;
        }

        // 对图片处理
        mOriginalBmp = handlerImage(originBmp, 0.7f);
        mOrigImageView.setImageBitmap(mOriginalBmp);

        // 初始化Views
        initGridViews();

        new Thread() {
            @Override
            public void run() {
                // 生成游戏数据
                generateGame();
            }
        }.start();
    }

    private void generateGame() {
        synchronized (this) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 切图 获取初始拼图数据 正常顺序
            ImagesUtil.createInitBitmaps(DIFFICULT, mOriginalBmp, this);

            // 生成随机数据
            GameUtil.getPuzzleGenerator(DIFFICULT);

            // 获取Bitmap集合
            mBlockItemList.clear();
            for (ItemBean temp : GameUtil.mItemBeans) {
                mBlockItemList.add(temp.getBitmap());
            }

            mHandler.sendEmptyMessageDelayed(2, 1000);
        }

    }

    private void initGridViews() {
        mGameGridView.setNumColumns(DIFFICULT);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(mOriginalBmp.getWidth(), mOriginalBmp.getHeight());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW, R.id.game_top_layout);
        params.addRule(RelativeLayout.ABOVE, R.id.game_bottom_layout);
        mGameGridView.setLayoutParams(params);
        mGameGridView.setHorizontalSpacing(0);
        mGameGridView.setVerticalSpacing(0);
        mGameGridView.setOnItemClickListener(this);
        mGameAdapter = new GridItemsAdapter(this, mBlockItemList);
        mGameGridView.setAdapter(mGameAdapter);
    }

    private Bitmap handlerImage(Bitmap originBmp, float scale) {
        // 获取屏幕尺寸
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeight = ScreenUtil.getScreenSize(this).heightPixels;

        // 将图片放大到固定尺寸
        return new ImagesUtil().resizeBitmap(
                screenWidth * scale,
                screenHeight * scale,
                originBmp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_orig_pic: {
                if (mIsShowImg) {
                    mOrigImageView.setVisibility(View.GONE);
                } else {
                    mOrigImageView.setVisibility(View.VISIBLE);
                }
                mIsShowImg = !mIsShowImg;
            }
            break;

            case R.id.btn_reset: {
                new Thread() {
                    @Override
                    public void run() {
                        generateGame();
                    }
                }.start();
            }
            break;

            case R.id.btn_back: {
                finish();
            }
            break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gv_puzzle_game: {
                // 判断是否可移动
                if (GameUtil.isMovable(position)) {
                }
            }
            break;

            default:
                break;
        }
    }
}
