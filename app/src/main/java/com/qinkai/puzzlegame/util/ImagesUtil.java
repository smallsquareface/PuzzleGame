package com.qinkai.puzzlegame.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.qinkai.puzzlegame.R;
import com.qinkai.puzzlegame.activity.PuzzleGame;
import com.qinkai.puzzlegame.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

public class ImagesUtil {

    /**
     * 处理图片 放大、缩小到合适位置
     *
     * @param newWidth  缩放后Width
     * @param newHeight 缩放后Height
     * @param bitmap    bitmap
     * @return bitmap
     */
    public Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(newWidth / bitmap.getWidth(), newHeight / bitmap.getHeight());
        return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true);
    }

    /**
     * 切图、初始状态（正常顺序）
     *
     * @param difficult 游戏种类
     * @param origBmp   选择的图片
     * @param context   context
     */
    public static void createInitBitmaps(int difficult, Bitmap origBmp, Context context) {

        GameUtil.mItemBeans.clear();

        List<Bitmap> bitmapItems = new ArrayList<>();
        // 每个Item的宽高
        int itemWidth = origBmp.getWidth() / difficult;
        int itemHeight = origBmp.getHeight() / difficult;

        for (int i = 0; i < difficult; i++) {
            for (int j = 0; j < difficult; j++) {
                Bitmap bitmap = Bitmap.createBitmap(origBmp,
                        itemWidth * j, itemHeight * i,
                        itemWidth, itemHeight);
                bitmapItems.add(bitmap);

                ItemBean itemBean = new ItemBean(
                        i * difficult + j,
                        i * difficult + j,
                        bitmap);
                GameUtil.mItemBeans.add(itemBean);
            }
        }

        // 保存最后一个图片在拼图完成时填充
        PuzzleGame.mLastBitmap = bitmapItems.get(difficult * difficult - 1);

        // 设置最后一个为空Item
        GameUtil.mItemBeans.remove(difficult * difficult - 1);
        bitmapItems.remove(difficult * difficult - 1);

        Bitmap blankBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank);
        blankBitmap = Bitmap.createBitmap(blankBitmap, 0, 0, itemWidth, itemHeight);
        bitmapItems.add(blankBitmap);
        GameUtil.mItemBeans.add(new ItemBean(difficult * difficult - 1, -1, blankBitmap));
        GameUtil.mBlankItemBean = GameUtil.mItemBeans.get(difficult * difficult - 1);
    }
}
