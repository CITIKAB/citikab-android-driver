package com.trioangle.goferdriver.helper;

/**
 * @package com.trioangle.goferdriver.helper
 * @subpackage helper
 * @category BottomNavigationViewHelper
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;

import com.trioangle.goferdriver.util.CommonMethods;

import java.lang.reflect.Field;

/* ************************************************************
                      BottomNavigationViewHelper
Its used Home page BottomNavigation function
*************************************************************** */
public class BottomNavigationViewHelper {

    public static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            CommonMethods.DebuggableLogE("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            CommonMethods.DebuggableLogE("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }
}
