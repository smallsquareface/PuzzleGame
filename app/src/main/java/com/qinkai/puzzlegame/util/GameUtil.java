package com.qinkai.puzzlegame.util;


import android.graphics.Bitmap;
import android.util.Log;

import com.qinkai.puzzlegame.activity.PuzzleGame;
import com.qinkai.puzzlegame.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {
    public static List<ItemBean> mItemBeans = new ArrayList<>();
    public static ItemBean mBlankItemBean;

    /**
     * 判断点击的Item是否可移动
     *
     * @param position position
     * @return 能否移动
     */
    public static boolean isMovable(int position) {
        int difficult = PuzzleGame.DIFFICULT;
        // 获取空格Item
        int blankId = mBlankItemBean.getItemId();

        // 不同行 相差为type
        if (Math.abs(blankId - position) == difficult) {
            return true;
        }
        // 相同行 相差为1
        if ((blankId / difficult == position / difficult) && (Math.abs(blankId - position) == 1)) {
            return true;
        }

        return false;
    }

    /**
     * 生成随机的Item
     */
    public static void getPuzzleGenerator(int DIFFICULT) {
        for (int i = 0; i < mItemBeans.size(); i++) {
            float random = (float) Math.random();
            int index = (int) (random * DIFFICULT * DIFFICULT) - 1;
            if (index < 0) index = 0;
            swapItems(mItemBeans.get(index), mBlankItemBean);
        }

        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getBitmapId());
        }
        // 判断生成是否有解
        if (!canSolve(data)) {
            getPuzzleGenerator(DIFFICULT);
        }
    }

    /**
     * 该数据是否有解
     *
     * @param data 拼图数组数据
     * @return 该数据是否有解
     */
    private static boolean canSolve(List<Integer> data) {
        // 获取空格Id
        int blankId = GameUtil.mBlankItemBean.getItemId();
        // 可行性原则
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
            // 从底往上数,空格位于奇数行
            if (((blankId - 1) / PuzzleGame.DIFFICULT) % 2 == 1) {
                return getInversions(data) % 2 == 0;
            } else {
                // 从底往上数,空位位于偶数行
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /**
     * 计算倒置和算法
     *
     * @param data 拼图数组数据
     * @return 该序列的倒置和
     */
    private static int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != -1 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }

    /**
     * 交换空格与点击Item的位置
     *
     * @param from  交换图
     * @param blank 空白图
     */
    private static void swapItems(ItemBean from, ItemBean blank) {
        // 交换BitmapId
        int bitmapID = from.getBitmapId();
        from.setBitmapId(blank.getBitmapId());
        blank.setBitmapId(bitmapID);

        // 交换Bitmap
        Bitmap bitmap = from.getBitmap();
        from.setBitmap(blank.getBitmap());
        blank.setBitmap(bitmap);

        // 设置新的Blank
        GameUtil.mBlankItemBean = from;
    }
}
