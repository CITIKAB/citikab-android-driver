package com.trioangle.goferdriver.payouts.payout_model_classed;

/**
 *
 * @package     com.makent.trioangle
 * @subpackage  adapter/host
 * @category    PayPalEmailAdapter
 * @author      Trioangle Product Team
 * @version     1.1
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.payouts.PayoutEmailListActivity;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.ConnectionDetector;
import com.trioangle.goferdriver.util.RequestCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;

@SuppressLint("ViewHolder")
public class PayPalEmailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ServiceListener {

	public final int TYPE_Explore = 0;
	public final int TYPE_LOAD = 1;

	static Context context;
	OnLoadMoreListener loadMoreListener;
	boolean isLoading = false, isMoreDataAvailable = true;

	protected static final String TAG = null;
	private ArrayList<PayoutDetail> modelItems;
	static  boolean check=false;
	protected boolean isInternetAvailable;
	int selected = -1;
	String payoutid,type,userid;
	public @Inject
	ApiService apiService;
	public @Inject
	CommonMethods commonMethods;
	public  @Inject
	SessionManager sessionManager;
	public PayPalEmailAdapter(Activity activity, Context context, ArrayList<PayoutDetail> Items) {
		this.context = context;
		this.modelItems = Items;

		ButterKnife.inject((Activity) context);
		AppController.getAppComponent().inject(this);
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if(viewType==TYPE_Explore){
			return new MovieHolder(inflater.inflate(R.layout.payout_paypal_list,parent,false));
		}else{
			return new LoadHolder(inflater.inflate(R.layout.row_load,parent,false));
		}
	}
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

		if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
			isLoading = true;
			loadMoreListener.onLoadMore();
		}

		if(getItemViewType(position)==TYPE_Explore){

			((MovieHolder)holder).bindData(modelItems.get(position),position);
		}
		//No else part needed as load holder doesn't bind any data
	}

	@Override
	public int getItemViewType(int position) {
		if(modelItems.get(position).getAccount_number().equals("load")){
			return TYPE_LOAD;
		}else{
			return TYPE_Explore;
		}
	}

	@Override
	public int getItemCount() {
		return modelItems.size();
	}


    /* VIEW HOLDERS */

	class MovieHolder extends RecyclerView.ViewHolder{
		TextView acc_number,payout_default,bankname;
		RelativeLayout paypalemailmore,paypalemailid;
		Button makedefault,delete;


		public MovieHolder(View itemView) {
			super(itemView);
			acc_number=(TextView) itemView.findViewById(R.id.acc_number);
			payout_default=(TextView) itemView.findViewById(R.id.paypal_ready);
			paypalemailid=(RelativeLayout) itemView.findViewById(R.id.paypalemailid);
			paypalemailmore=(RelativeLayout) itemView.findViewById(R.id.paypalemailmore);
			delete=(Button) itemView.findViewById(R.id.paypal_delete);
			bankname=(TextView)itemView.findViewById(R.id.bank_name);
			makedefault=(Button) itemView.findViewById(R.id.paypal_default);

		}

		void bindData(final PayoutDetail movieModel, final int position){

			acc_number.setText(movieModel.getAccount_number().toString());
			bankname.setText(movieModel.getBank_name());
			final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

			if(movieModel.getIsDefault().equals("No")) {
				payout_default.setVisibility(View.GONE);
			}else
			{
				payout_default.setVisibility(View.VISIBLE);
			}

			if(selected==position){
				paypalemailmore.setClickable(true);
				if(context.getResources().getString(R.string.layout_direction).equals("0")){
					paypalemailid.animate().translationX(-(screenWidth / 2 + 220)).setDuration(200);
				}else{
					paypalemailid.animate().translationX((screenWidth / 2 + 100)).setDuration(200);
				}
			}else{
				paypalemailmore.setClickable(false);
				paypalemailid.animate().translationX(0).setDuration(200);
			}
			paypalemailmore.setClickable(false);

			paypalemailid.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(movieModel.getIsDefault().equals("No")) {
						if (!paypalemailmore.isClickable()) {
							paypalemailmore.setClickable(true);
							//paypalemailid.animate().translationX(-(screenWidth / 2 + 100)).setDuration(200);
							if(selected==position){
								selected = -1;
							}else{
								selected = position;
							}
							notifyDataSetChanged();
						} else {
							paypalemailmore.setClickable(false);
							//paypalemailid.animate().translationX(0).setDuration(200);
							selected = -1;
						}
					}else
					{

					}
				}
			});
			delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					isInternetAvailable = getNetworkState().isConnectingToInternet();
					if (isInternetAvailable) {
						userid = sessionManager.getAccessToken();
						payoutid = movieModel.getPayoutId().toString();
						type = "delete";
						UpdatePayoutDetails();
						//	payoutemail.payoutOption(view,position,0); // // 0  delete and 1 to set default
						paypalemailmore.setClickable(false);
						paypalemailid.animate().translationX(0).setDuration(200);
						//notifyItemRemoved(getAdapterPosition());
					}else {
						Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
			});
			makedefault.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					isInternetAvailable = getNetworkState().isConnectingToInternet();
					if (isInternetAvailable) {
						userid = sessionManager.getAccessToken();
						payoutid = movieModel.getPayoutId().toString();
						type = "default";
						UpdatePayoutDetails();
						paypalemailmore.setClickable(false);
						paypalemailid.animate().translationX(0).setDuration(200);
					}else {
						Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	static class LoadHolder extends RecyclerView.ViewHolder{
		public LoadHolder(View itemView) {
			super(itemView);
		}
	}

	public void setMoreDataAvailable(boolean moreDataAvailable) {
		isMoreDataAvailable = moreDataAvailable;
	}

	/* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
	public void notifyDataChanged(){
		notifyDataSetChanged();
		isLoading = false;
	}


	public interface OnLoadMoreListener{
		void onLoadMore();
	}

	public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
		this.loadMoreListener = loadMoreListener;
	}

	@Override
	public void onSuccess(JsonResponse jsonResp, String data) {
	    commonMethods.hideProgressDialog();
		if (jsonResp.isSuccess()) {

			Intent x = new Intent(context, PayoutEmailListActivity.class);
			//activity.setResult(Activity.RESULT_OK,x);
			((Activity)context).finish();
			context.startActivity(x);
		} else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
		}
	}

	@Override
	public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
	}

	public void UpdatePayoutDetails(){
		apiService.payoutChanges(userid,payoutid,type).enqueue(new RequestCallback(this));
	}


	public ConnectionDetector getNetworkState() {
		ConnectionDetector connectionDetector = new ConnectionDetector(context);
		return connectionDetector;
	}

}