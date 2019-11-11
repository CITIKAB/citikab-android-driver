package com.trioangle.goferdriver.custompalette;
/**
 * @package com.trioangle.goferdriver.custompalette
 * @subpackage custompalette
 * @category FontTextView
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/* ************************************************************
                   Its used for FontTextView
*************************************************************** */
@SuppressLint("AppCompatCustomView")
public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}
