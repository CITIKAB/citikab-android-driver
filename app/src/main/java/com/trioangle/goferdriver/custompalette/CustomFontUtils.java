package com.trioangle.goferdriver.custompalette;
/**
 * @package com.trioangle.goferdriver.custompalette
 * @subpackage custompalette
 * @category CustomFontUtils
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trioangle.goferdriver.R;


public class CustomFontUtils {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView);

        String fontName = attributeArray.getString(R.styleable.FontTextView_fontname);



        // check if a special textStyle was used (e.g. extra bold)
        int textStyle = attributeArray.getInt(R.styleable.FontTextView_textStyle, 0);

        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        }

        Typeface customFont = selectTypeface(context, fontName);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    public static void applyCustomFont(EditText customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView);

        String fontName = attributeArray.getString(R.styleable.FontTextView_fontname);


        // check if a special textStyle was used (e.g. extra bold)
        int textStyle = attributeArray.getInt(R.styleable.FontTextView_textStyle, 0);

        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        }

        Typeface customFont = selectTypeface(context, fontName);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    public static void applyCustomFont(Button customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView);

        String fontName = attributeArray.getString(R.styleable.FontTextView_fontname);


        // check if a special textStyle was used (e.g. extra bold)
        int textStyle = attributeArray.getInt(R.styleable.FontTextView_textStyle, 0);

        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        }

        Typeface customFont = selectTypeface(context, fontName);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    private static Typeface selectTypeface(Context context, String fontName) {
        if(TextUtils.isEmpty(fontName)){
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium), context);
        }
        if (fontName.contentEquals(context.getResources().getString(R.string.font_PermanentMarker))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_PermanentMarker), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_Book))) {

            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_Book), context);

        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_Medium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_Medium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_NarrBook))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_NarrBook), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_NarrMedium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_NarrMedium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_NarrNews))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_NarrNews), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_News))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_News), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_WideMedium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_WideMedium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_WideNews))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_WideNews), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UBERBook))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERBook), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UBERMedium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UBERNews))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERNews), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UberClone))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UberClone), context);
        } else {
            // no matching font found
            // return null so Android just uses the standard font (Roboto)
            //return null;
            // temporaryly returning this uber medium font
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium), context);
        }
    }
}