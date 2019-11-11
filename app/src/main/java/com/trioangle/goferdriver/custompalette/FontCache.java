package com.trioangle.goferdriver.custompalette;
/**
 * @package com.trioangle.goferdriver.custompalette
 * @subpackage custompalette
 * @category FontCache
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
/* ************************************************************
                   Its used for FontCache
*************************************************************** */

public class FontCache {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontname);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }
}
