package com.cometchat.pro.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import com.cometchat.pro.uikit.R;

@BindingMethods(value = {@BindingMethod(type = Avatar.class, attribute = "app:count", method = "setCount")})
public class BadgeCount extends LinearLayout {

    private TextView tvCount;

    private int count;

    private float countSize;

    private int countColor;

    private int countBackgroundColor;

    public BadgeCount(Context context) {
        super(context);
        initViewComponent(context, null, -1);
    }

    public BadgeCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewComponent(context, attrs, -1);
    }

    public BadgeCount(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewComponent(context, attrs, defStyleAttr);
    }

    private void initViewComponent(Context context, AttributeSet attributeSet, int defStyleAttr) {

        View view = View.inflate(context, R.layout.cc_badge_count, null);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.BadgeCount,
                0, 0);
        count = a.getInt(R.styleable.BadgeCount_count, 0);
        countSize = a.getDimension(R.styleable.BadgeCount_count_size, 12);
        countColor = a.getColor(R.styleable.BadgeCount_count_color, Color.WHITE);
        countBackgroundColor=a.getColor(R.styleable.BadgeCount_count_background_color,getResources().getColor(R.color.cc_primary));


        addView(view);

          if (count==0){
              setVisibility(INVISIBLE);
          }else {
              setVisibility(VISIBLE);
          }

        tvCount = view.findViewById(R.id.tvSetCount);
        tvCount.setBackground(getResources().getDrawable(R.drawable.count_background));
        tvCount.setTextSize(countSize);
        tvCount.setTextColor(countColor);
        tvCount.setText(String.valueOf(count));
        setCountBackground(countBackgroundColor);

    }

    public void setCountBackground(@ColorInt int color) {
        Drawable unwrappedDrawable = tvCount.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable,color);
    }

    public void setCountColor(@ColorInt int color) {
        tvCount.setTextColor(color);
    }

    public void setCountSize(float size) {
        tvCount.setTextSize(size);

    }

    public void setCount(int count) {
        this.count = count;
        if (count == 0)
            setVisibility(GONE);
        else
            setVisibility(View.VISIBLE);
        if (count<999)
            tvCount.setText(String.valueOf(count));
        else
            tvCount.setText("999+");
    }


}
