package com.trioangle.goferdriver.custompalette;
/**
 * @package com.trioangle.goferdriver.custompalette
 * @subpackage custompalette
 * @category FontEditText
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/* ************************************************************
                   Its used for FontEditText
*************************************************************** */
public class FontEditText extends EditText {

    public FontEditText(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}
