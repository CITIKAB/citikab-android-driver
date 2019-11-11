package com.trioangle.goferdriver.map.drawpolyline;

/**
 * @package com.trioangle.goferdriver
 * @subpackage map.drawpolyline
 * @category PolylineOptionsInterface
 * @author Trioangle Product Team
 * @version 1.5
 */

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public interface PolylineOptionsInterface {
    void getPolylineOptions(PolylineOptions output, ArrayList points);
}
