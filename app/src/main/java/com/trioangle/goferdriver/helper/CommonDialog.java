package com.trioangle.goferdriver.helper;
/**
 * @package com.trioangle.goferdriver.helper
 * @subpackage helper
 * @category CommonDialog
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.rating.Riderrating;

import javax.inject.Inject;

/* ************************************************************
                      CommonDialog
Its used for commondialog screen    (Like Arrive now, Begin trip, Payment completed)
*************************************************************** */

    public class CommonDialog extends Activity implements View.OnClickListener {


        int status;
        String setMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common_dialog);

        status=getIntent().getIntExtra("status",0);

        this.setFinishOnTouchOutside(false);
        if (status==1){
            setMessage=getResources().getString(R.string.yourtripcanceledrider);
        }else if (status==2){
            setMessage=getResources().getString(R.string.paymentcompleted);
        }
        TextView message = (TextView) findViewById(R.id.message);
        System.out.println("getMessag "+setMessage);
        message.setText(setMessage);
        Button ok_btn = (Button) findViewById(R.id.ok_btn_id);
        ok_btn.setOnClickListener(this);
    }

    /*
    *  Get driver rating and feed back details API Called
    */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ok_btn_id:

                if (getIntent().getIntExtra("type", 0) == 0) {
                    Intent requestaccept = new Intent(getApplicationContext(), Riderrating.class);
                    requestaccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(requestaccept);
                }else{
                    Intent requestaccept = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(requestaccept);
                }
                this.finish();
                break;
            default:
                break;
        }
    }
}
