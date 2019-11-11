package com.trioangle.goferdriver.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.goferdriver.R;

import java.util.List;

/**
 * Created by trioangle on 9/7/18.
 */

@SuppressLint("ValidFragment")
public class CustomDialog extends BaseDialogFragment {
    private String title = "", message = "", positiveBtnTxt = "", negativeBtnTxt = "", confirmTxt = "";
    private int index = -1;
    private TextView tvAllow, tvDeny;
    private btnAllowClick allowClickListener;
    private btnDenyClick denyClickListener;
    private listItemsClick listItemsClickListener;
    private popUpItemsClick popUpItemsClickListener;
    private ListPopupWindow listPopupWindow;
    private boolean isProgressDialog = false;
    private RelativeLayout prgresslayout;
    private RelativeLayout alertDialogLayout;


    public CustomDialog() {
    }

    public CustomDialog(String message, String confirmTxt, btnAllowClick okClickListener) {
        this.message = message;
        this.confirmTxt = confirmTxt;
        this.allowClickListener = okClickListener;
        this.mActivity = null;
        this.index = -1;
        setLayoutId(R.layout.activity_custom_dialog);
    }

    public CustomDialog(boolean isProgressDialog) {
        this.isProgressDialog = isProgressDialog;
        this.mActivity = null;
        this.index = -1;
        setAnimation(false);
        setLayoutId(R.layout.activity_custom_dialog);
    }

    public CustomDialog(String message, String positiveBtnTxt, String negativeBtnTxt, btnAllowClick allowClickListener, btnDenyClick denyClickListener) {
        this.message = message;
        this.confirmTxt = "";
        this.positiveBtnTxt = positiveBtnTxt;
        this.negativeBtnTxt = negativeBtnTxt;
        this.allowClickListener = allowClickListener;
        this.denyClickListener = denyClickListener;
        this.mActivity = null;
        this.index = -1;
        setLayoutId(R.layout.activity_custom_dialog);
    }


    public CustomDialog(String title, String message, String confirmTxt, btnAllowClick okClickListener) {
        this.title = title;
        this.message = message;
        this.confirmTxt = confirmTxt;
        this.allowClickListener = okClickListener;
        this.mActivity = null;
        this.index = -1;
        setLayoutId(R.layout.activity_custom_dialog);
    }

    public CustomDialog(String title, String message, String positiveBtnTxt, String negativeBtnTxt, btnAllowClick allowClickListener, btnDenyClick denyClickListener) {
        this.title = title;
        this.message = message;
        this.confirmTxt = "";
        this.positiveBtnTxt = positiveBtnTxt;
        this.negativeBtnTxt = negativeBtnTxt;
        this.allowClickListener = allowClickListener;
        this.denyClickListener = denyClickListener;
        this.mActivity = null;
        this.index = -1;
        setLayoutId(R.layout.activity_custom_dialog);
    }

    public CustomDialog(int index, String title, String message, String positiveBtnTxt, String negativeBtnTxt, btnAllowClick allowClickListener, btnDenyClick denyClickListener) {
        this.title = title;
        this.message = message;
        this.confirmTxt = "";
        this.positiveBtnTxt = positiveBtnTxt;
        this.negativeBtnTxt = negativeBtnTxt;
        this.allowClickListener = allowClickListener;
        this.denyClickListener = denyClickListener;
        this.mActivity = null;
        this.index = index;
        setAnimation(false);
        setLayoutId(R.layout.activity_custom_dialog);
    }

    public void showListDialog(Context context, List<String> listItems, final listItemsClick listener) {
        this.listItemsClickListener = listener;
        AlertDialog.Builder listAlertDialogBuilder = new AlertDialog.Builder(context);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(listItems);
        listAlertDialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listItemsClickListener.clicked(which);
            }
        });
        listAlertDialogBuilder.show();
    }

    public void showListPopup(Context context, List<String> options, View anchorView, popUpItemsClick listener) {

        this.popUpItemsClickListener = listener;
        listPopupWindow = new ListPopupWindow(context);
        listPopupWindow.setAnchorView(anchorView);
        listPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.white)));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listPopupWindow.setAdapter(dataAdapter);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popUpItemsClickListener.clicked(parent.getItemAtPosition(position).toString());
                if (listPopupWindow != null && listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                }
            }
        });
        listPopupWindow.show();
    }

    @Override
    public void initViews(View v) {
        super.initViews(v);
        TextView tvTitle = (TextView) v.findViewById(R.id.tv_dialog_title);
        TextView tvMessage = (TextView) v.findViewById(R.id.tv_message);
        this.tvAllow = (TextView) v.findViewById(R.id.tv_allow);
        this.tvDeny = (TextView) v.findViewById(R.id.tv_deny);
        this.alertDialogLayout = (RelativeLayout) v.findViewById(R.id.rlt_alert_dialog_layout);
        this.prgresslayout = (RelativeLayout) v.findViewById(R.id.llt_progress_dialog);
        tvMessage.setText(message);

        if (isProgressDialog) {
            this.prgresslayout.setVisibility(View.VISIBLE);
            this.alertDialogLayout.setVisibility(View.GONE);
        } else {
            this.prgresslayout.setVisibility(View.GONE);
            this.alertDialogLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
                tvTitle.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(confirmTxt)) {
                this.tvDeny.setVisibility(View.GONE);
                this.tvAllow.setVisibility(View.VISIBLE);
                this.tvAllow.setText(confirmTxt);
            } else {
                this.tvAllow.setVisibility(View.VISIBLE);
                this.tvDeny.setVisibility(View.VISIBLE);
                this.tvAllow.setText(positiveBtnTxt);
                this.tvDeny.setText(negativeBtnTxt);
            }

            if (index >= 0) {
                this.tvAllow.setTextColor(ContextCompat.getColor(mActivity, R.color.app_background));
            }
        }

        initEvent();
        setCancelable(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = context instanceof Activity ? (Activity) context : null;
    }

    private void initEvent() {
        tvAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowClickListener != null) {
                    allowClickListener.clicked();
                }
                dismiss();
            }
        });

        tvDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (denyClickListener != null) {
                    denyClickListener.clicked();
                }
                dismiss();
            }
        });
    }

    public interface btnAllowClick {
        void clicked();
    }


    public interface btnDenyClick {
        void clicked();
    }

    public interface listItemsClick {
        int clicked(int which);
    }

    public interface popUpItemsClick {
        String clicked(String selectedItem);
    }
}

