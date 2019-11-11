package com.trioangle.goferdriver.rating;
/**
 * @package com.trioangle.goferdriver.rating
 * @subpackage rating
 * @category CommentsRecycleAdapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

/* ************************************************************
                CommentsRecycleAdapter
Its used to view the feedback comments with rider screen page function
*************************************************************** */
public class CommentsRecycleAdapter extends RecyclerView.Adapter<CommentsRecycleAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> feedbackarraylist;

    public @Inject
    SessionManager sessionManager;

    public CommentsRecycleAdapter(ArrayList<HashMap<String, String>> feedbackarraylist) {
        this.feedbackarraylist = feedbackarraylist;

    }

    @Override
    public CommentsRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.commant_cards_layout, viewGroup, false);

        AppController.getAppComponent().inject(this);
        return new ViewHolder(view);
    }

    /*
   *  Get rider feedback list bind
   */
    @Override
    public void onBindViewHolder(CommentsRecycleAdapter.ViewHolder viewHolder, int i) {

        viewHolder.comant.setText(feedbackarraylist.get(i).get("rating_comments"));
        viewHolder.date.setText(feedbackarraylist.get(i).get("date"));
        viewHolder.go_rating.setRating(Float.parseFloat(feedbackarraylist.get(i).get("rating")));
        DateFormat originalFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        if (sessionManager.getLanguageCode().equals("es")){
            targetFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
        }else if (sessionManager.getLanguageCode().equals("fa")){
            targetFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("fa", "AF"));
        }else if (sessionManager.getLanguageCode().equals("ar")){
            targetFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ar", "DZ"));
        }
        Date date = null;
        try {
            date = originalFormat.parse(feedbackarraylist.get(i).get("date"));
            String dat  =targetFormat.format(date);



            viewHolder.date.setText(dat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return feedbackarraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RatingBar go_rating;
        private TextView comant;
        private TextView date;

        public ViewHolder(View view) {
            super(view);

            comant = (TextView) view.findViewById(R.id.comant);
            date = (TextView) view.findViewById(R.id.date);
            go_rating = (RatingBar) view.findViewById(R.id.go_rating);
        }
    }


}
