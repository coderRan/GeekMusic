package com.zdr.geekmusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.zdr.geekmusic.entity.Lrc;

/**
 * 显示歌词的TextView
 * Created by zdr on 16-9-18.
 */
public class LrcTextView extends TextView {
    private float width;    //歌词视图宽度
    private float height;    //歌词视图高度
    private float divide;    //歌词间距
    private Paint currentPaint;    //当前画笔对象
    private Paint notCurrentPaint;    //非当前画笔对象
    private float textHeight = 50;    //文本高度
    private float textSize = 48;        //文本大小
    private int index = 0;    //list集合下标

    private Lrc mLrc;

    public LrcTextView(Context context) {
        super(context);
        init();
    }

    public LrcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(true);        //设置可对焦

        //高亮部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        currentPaint.setColor(Color.argb(255, 255, 225, 0));
        currentPaint.setTextSize(64);
        currentPaint.setTypeface(Typeface.SERIF);

        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);
        try {
            if (mLrc == null) {
                canvas.drawText("歌词不存在", width / 2, height / 2, currentPaint);
                return;
            }
            int size = mLrc.getLrcItems().size();
            setText("");

            canvas.drawText(mLrc.getLrcItems().get(index).getContext(),
                    width / 2, height / 2, currentPaint);
            float tempY = height / 2;
            //画出本句之前的句子
            int temp = index - 4 >= 0 ? index - 4 : 0;
            for (int i = index - 1; i >= temp; i--) {
                //向上推移
                tempY = tempY - textHeight - divide;
                notCurrentPaint.setColor(Color.argb(255 - (index - i) * 30, 230, 230, 230));
                canvas.drawText(mLrc.getLrcItems().get(i).getContext(),
                        width / 2, tempY, notCurrentPaint);
            }
            tempY = height / 2;
            //画出本句之后的句子
            temp = index + 5 <= size ? index + 5 : size;
            for (int i = index + 1; i < temp; i++) {
                //往下推移
                tempY = tempY + textHeight + divide;
                notCurrentPaint.setColor(Color.argb(255 - (i - index) * 30, 230, 230, 230));
                canvas.drawText(mLrc.getLrcItems().get(i).getContext(),
                        width / 2, tempY, notCurrentPaint);
            }
        } catch (NullPointerException e) {
            Log.e("onDraw", "onDraw: " + e.toString());
        }


    }

    /**
     * 当view大小改变的时候调用的方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        divide = (height - 25 * 9) / 11;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setLrc(Lrc lrc) {
        mLrc = lrc;
    }
}
