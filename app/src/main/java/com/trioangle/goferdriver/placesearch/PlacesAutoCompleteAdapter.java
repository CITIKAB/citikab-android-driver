package com.trioangle.goferdriver.placesearch;

/**
 * @package com.trioangle.gofer
 * @subpackage placesearch
 * @category placeseach auto complete
 * @author Trioangle Product Team
 * @version 1.1
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.util.CommonMethods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Note that this adapter requires a valid {@link GoogleApiClient}.
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the {@link Places#GEO_DATA_API} API.
 **/
public class PlacesAutoCompleteAdapter
        extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {

    private ArrayList<PlaceAutocomplete> mResultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;
    private Context mContext;
    private int layout;

    public PlacesAutoCompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                     LatLngBounds bounds, AutocompleteFilter filter) {
        mContext = context;
        layout = resource;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null && mResultList.size() > 0) {
                        // The API successfully returned results.


                        results.values = mResultList;
                        results.count = mResultList.size();

                    } else {
                        results.count = 0;
                        return null;
                    }
                } else {
                    results.count = 0;
                    return null;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // mResultList.clear();
                    notifyDataSetChanged();
                    // The API did not return any results, invalidate the data set.
                    // PlacesAutoCompleteAdapter.notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            CommonMethods.DebuggableLogI("", "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(500, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                // Toast.makeText(mContext, "Error contacting API: " + status.toString(),Toast.LENGTH_SHORT).show();
                CommonMethods.DebuggableLogE("", "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            CommonMethods.DebuggableLogI("", "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            if (autocompletePredictions.getCount() > 0) {
                Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
                while (iterator.hasNext()) {
                    AutocompletePrediction prediction = iterator.next();
                    // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getFullText(null), prediction.getPrimaryText(null)));
                }

                // Release the buffer now that all data has been copied.
                autocompletePredictions.release();

                return resultList;
            } else {
                return null;
            }
        }
        CommonMethods.DebuggableLogE("", "Google API client is not connected for autocomplete query.");
        return null;
    }

    @Override
    public PredictionHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(layout, viewGroup, false);
        PredictionHolder mPredictionHolder = new PredictionHolder(convertView);
        return mPredictionHolder;
    }

    @Override
    public void onBindViewHolder(PredictionHolder mPredictionHolder, final int i) {

        String Address = (String) mResultList.get(i).description;

        String[] address = Address.split(",");
        String subaddress = "";
        if (address.length > 0) {
            for (int j = 1; j < address.length; j++) {
                try {
                    if (j == 1) {
                        if (address.length > 1) {
                            subaddress = address[1];
                        } else {
                            subaddress = "";
                        }
                    } else {
                        subaddress = subaddress + ", " + address[j];
                    }

                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
            mPredictionHolder.mPrediction.setText(address[0]);
            mPredictionHolder.mPredictionSub.setText(subaddress);
        }
    }

    @Override
    public int getItemCount() {
        if (mResultList != null)
            return mResultList.size();
        else
            return 0;
    }

    public PlaceAutocomplete getItem(int position) {
        try {
            if (mResultList.size() > 0) {
                return mResultList.get(position);
            } else {
                return null;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public class PredictionHolder extends RecyclerView.ViewHolder {
        private TextView mPrediction, mPredictionSub;
        private RelativeLayout mRow;

        public PredictionHolder(View itemView) {

            super(itemView);
            mPrediction = (TextView) itemView.findViewById(R.id.address);
            mPredictionSub = (TextView) itemView.findViewById(R.id.address_secondry);
            mRow = (RelativeLayout) itemView.findViewById(R.id.predictedRow);
        }

    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;
        public CharSequence addresss;

        PlaceAutocomplete(CharSequence placeId, CharSequence description, CharSequence addresss) {
            this.placeId = placeId;
            this.description = description;
            this.addresss = addresss;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }


}