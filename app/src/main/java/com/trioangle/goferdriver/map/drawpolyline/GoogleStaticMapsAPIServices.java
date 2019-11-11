package com.trioangle.goferdriver.map.drawpolyline;

/**
 * @package com.trioangle.goferdriver
 * @subpackage map.drawpolyline
 * @category GoogleStaticMapsAPIServices
 * @author Trioangle Product Team
 * @version 1.5
 */

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class GoogleStaticMapsAPIServices {

    private static String GOOGLE_STATIC_MAPS_API_KEY = "AIzaSyDhWyp5Atv9H9lTEsXrMAzdPmZF429m9PE";

    public static String getStaticMapURL(LatLng pikcuplatlng, LatLng droplatlng, List<LatLng> points) {
        String pathString = "";

        if (points.size() > 0) {
            String encodedPathLocations = PolyUtil.encode(points);
            pathString = "&path=color:0x000000ff%7Cweight:4%7Cenc:" + encodedPathLocations;
        }

        String positionOnMap = "";// = "&markers=size:mid|icon:http://demo.trioangle.com/gofer/public/images/pickup.png|" + pickupstr;
        String positionOnMap1 = "";// = "&markers=size:mid|icon:"+getResources().getString(R.string.imageurl)+"drop.png|" + dropstr;


        String staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x480&" +
                pikcuplatlng.latitude + "," + pikcuplatlng.longitude +
                pathString + "" + positionOnMap + "" + positionOnMap1 + "&zoom=15" +
                "&key=" + GOOGLE_STATIC_MAPS_API_KEY;

        return staticMapURL;
    }


}
