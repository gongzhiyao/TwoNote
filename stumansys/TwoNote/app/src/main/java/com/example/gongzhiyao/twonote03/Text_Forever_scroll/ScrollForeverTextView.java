package com.example.gongzhiyao.twonote03.Text_Forever_scroll;

/**
 * Created by 宫智耀 on 2016/4/24.
 */

/**
 * 在这里自定义了一个Textview用于实现跑马灯效果
 */



import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ScrollForeverTextView extends TextView {

    public ScrollForeverTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ScrollForeverTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollForeverTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
