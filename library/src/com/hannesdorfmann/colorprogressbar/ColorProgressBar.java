package com.hannesdorfmann.colorprogressbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.util.AttributeSet;

public class ColorProgressBar extends android.widget.ProgressBar{

    /**
     * Indicates that the simple mode should be applied, where the native android ProgressBar gets colored
     */
    public static final int MODE_SIMPLE = 1;

    /**
     * A single loading circle will be used as drawable
     */
    public static final int MODE_SINGLE= 2;

    /**
     * A loading spinner drawable will be used that will look similar to the native Progressbar but can be customized in with colors
     */
    public static final int MODE_CUSTOM = 3;

    public ColorProgressBar(Context context) {
        super(context);
        init(context, null, null);
    }

    public ColorProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null);
    }

    public ColorProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    /**
     * Initializises the look
     * @param context
     * @param attributeSet
     * @param defStyle
     */
    protected void init(Context context, AttributeSet attributeSet, Integer defStyle){

        setIndeterminate(true);

        // Read xml styles
        TypedArray a = null;
        if (attributeSet != null){

            if (defStyle != null){
                 a = context.obtainStyledAttributes(attributeSet, R.styleable.ColorProgressBar, defStyle, 0);
            } else {
                a = context.obtainStyledAttributes(attributeSet, R.styleable.ColorProgressBar);
            }

            applyMode(a);
            a.recycle();

        } else {
             // No attribute set, so set everything to the default

        }

    }


    /**
     * Determines the mode and applies the styling
     * @param array
     */
    private void applyMode(TypedArray array){

        int mode = array.getInt(R.styleable.ColorProgressBar_mode, MODE_SIMPLE);

        if (MODE_SIMPLE == mode){
            applySimpleMode(array);
        } else if (MODE_SINGLE == mode){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                applySingleMode(array);
            } else {
                applySimpleMode(array);
            }
        } else if (MODE_CUSTOM == mode){
            applyCustomMode(array);
        } else {
            throw new IllegalArgumentException("Unknown mode for with value "+mode);
        }

    }


    /**
     * Applies the simple mode
     * @param array
     */
    protected void applySimpleMode(TypedArray array){

        int color = array.getColor(R.styleable.ColorProgressBar_color, -1 );

        if (color != -1){
            if (isOverrideAndroid2(array)){
                // Also Android 2.x drawable should be colorized
                Drawable d = getIndeterminateDrawable();
                d.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

            } else {

                // Colorize only on Honeycomb (Android 3) and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    Drawable d = getIndeterminateDrawable();
                    d.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                }
            }
        }
        // else no color set, so do nothing and use the default

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void applySingleMode(TypedArray array){

        RotateDrawable d = (RotateDrawable) getResources().getDrawable(R.drawable.spinner_single);
        GradientDrawable gradient = (GradientDrawable) d.getDrawable();

        int startColor = array.getColor(R.styleable.ColorProgressBar_startColor, -1);
        int middleColor = array.getColor(R.styleable.ColorProgressBar_middleColor, -1);
        int endColor = array.getColor(R.styleable.ColorProgressBar_endColor, -1);


        if (startColor == -1){
            throw new IllegalArgumentException("You have not specified a start color");
        }

        if (endColor == -1){
            endColor = getResources().getColor(R.color.spinner_single_default_end);
        }

        if (middleColor == -1){
            middleColor = getResources().getColor(R.color.spinner_single_default_middle);
        }

       gradient.setColors(new int[]{startColor, endColor, middleColor});
        setIndeterminateDrawable(d);

    }



    protected  void applyCustomMode(TypedArray array){

    }

    private boolean isOverrideAndroid2(TypedArray array){
        return array.getBoolean(R.styleable.ColorProgressBar_overrideAndroid2, false);
    }

}