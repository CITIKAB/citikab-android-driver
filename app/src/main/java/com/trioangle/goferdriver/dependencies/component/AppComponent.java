package com.trioangle.goferdriver.dependencies.component;
/**
 * @package com.trioangle.com.trioangle.goferdriver
 * @subpackage dependencies.component
 * @category AppComponent
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.trioangle.goferdriver.database.AddFirebaseDatabase;
import com.trioangle.goferdriver.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.dependencies.module.AppContainerModule;
import com.trioangle.goferdriver.dependencies.module.ApplicationModule;
import com.trioangle.goferdriver.dependencies.module.ImageCompressAsyncTask;
import com.trioangle.goferdriver.dependencies.module.NetworkModule;
import com.trioangle.goferdriver.firebaseChat.ActivityChat;
import com.trioangle.goferdriver.firebaseChat.AdapterFirebaseRecylcerview;
import com.trioangle.goferdriver.firebaseChat.FirebaseChatHandler;
import com.trioangle.goferdriver.firebaseChat.FirebaseChatNotificationService;
import com.trioangle.goferdriver.fragments.AccountFragment;
import com.trioangle.goferdriver.fragments.EarningFragment;
import com.trioangle.goferdriver.fragments.HomeFragment;
import com.trioangle.goferdriver.fragments.RatingFragment;
import com.trioangle.goferdriver.fragments.currency.CurrencyListAdapter;
import com.trioangle.goferdriver.fragments.language.LanguageAdapter;
import com.trioangle.goferdriver.fragments.payment.AddPayment;

import com.trioangle.goferdriver.fragments.payment.PaymentPage;
import com.trioangle.goferdriver.helper.CommonDialog;
import com.trioangle.goferdriver.helper.RunTimePermission;
import com.trioangle.goferdriver.home.CancelYourTripActivity;
import com.trioangle.goferdriver.home.RequestAcceptActivity;
import com.trioangle.goferdriver.home.RequestReceiveActivity;
import com.trioangle.goferdriver.home.RiderContactActivity;
import com.trioangle.goferdriver.home.RiderProfilePage;
import com.trioangle.goferdriver.map.GpsService;
import com.trioangle.goferdriver.map.drawpolyline.DownloadTask;
import com.trioangle.goferdriver.paymentstatement.DailyEarningDetails;
import com.trioangle.goferdriver.paymentstatement.PayStatementDetails;
import com.trioangle.goferdriver.paymentstatement.PaymentStatementActivity;
import com.trioangle.goferdriver.paymentstatement.TripEarningDetails;
import com.trioangle.goferdriver.payouts.BankDetailsActivity;
import com.trioangle.goferdriver.payouts.PayoutAddressDetailsActivity;
import com.trioangle.goferdriver.payouts.PayoutBankDetailsActivity;
import com.trioangle.goferdriver.payouts.PayoutCoutryListAdapter2;
import com.trioangle.goferdriver.payouts.PayoutEmailActivity;
import com.trioangle.goferdriver.payouts.PayoutEmailListActivity;
import com.trioangle.goferdriver.payouts.adapter.PayoutCountryListAdapter;
import com.trioangle.goferdriver.payouts.payout_model_classed.PayPalEmailAdapter;
import com.trioangle.goferdriver.profile.DriverProfile;
import com.trioangle.goferdriver.profile.VehiclInformation;
import com.trioangle.goferdriver.pushnotification.MyFirebaseInstanceIDService;
import com.trioangle.goferdriver.pushnotification.MyFirebaseMessagingService;
import com.trioangle.goferdriver.rating.Comments;
import com.trioangle.goferdriver.rating.CommentsRecycleAdapter;
import com.trioangle.goferdriver.rating.PaymentAmountPage;
import com.trioangle.goferdriver.rating.RiderFeedBack;
import com.trioangle.goferdriver.rating.Riderrating;
import com.trioangle.goferdriver.service.LocationService;
import com.trioangle.goferdriver.service.UpdateGPSWorker;
import com.trioangle.goferdriver.signinsignup.DocHomeActivity;
import com.trioangle.goferdriver.signinsignup.DocumentUploadActivity;
import com.trioangle.goferdriver.signinsignup.MobileActivity;
import com.trioangle.goferdriver.signinsignup.Register;
import com.trioangle.goferdriver.signinsignup.RegisterCarDetailsActivity;
import com.trioangle.goferdriver.signinsignup.RegisterOTPActivity;
import com.trioangle.goferdriver.signinsignup.ResetPassword;
import com.trioangle.goferdriver.signinsignup.SigninActivity;
import com.trioangle.goferdriver.signinsignup.SigninSignupHomeActivity;
import com.trioangle.goferdriver.splash.SplashActivity;
import com.trioangle.goferdriver.tripsdetails.Past;
import com.trioangle.goferdriver.tripsdetails.TripDetails;
import com.trioangle.goferdriver.tripsdetails.TripsAdapter;
import com.trioangle.goferdriver.tripsdetails.Upcoming;
import com.trioangle.goferdriver.tripsdetails.YourTrips;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import net.hockeyapp.android.utils.ImageUtils;

import javax.inject.Singleton;

import dagger.Component;


/*****************************************************************
 App Component
 ****************************************************************/
@Singleton
@Component(modules = {NetworkModule.class, ApplicationModule.class, AppContainerModule.class})
public interface AppComponent {
    // ACTIVITY

    void inject(BankDetailsActivity bankDetailsActivity);

    void inject(CurrencyListAdapter currencyListAdapter);

    void inject(PayoutEmailActivity payoutEmailActivity);

    void inject(PayoutEmailListActivity payoutEmailListActivity);

    void inject(PayPalEmailAdapter payPalEmailAdapter);

    void inject(PayoutAddressDetailsActivity payoutAddressDetailsActivity);

    void inject(PayoutBankDetailsActivity payoutBankDetailsActivity);

    void inject(PayoutCoutryListAdapter2 payoutCoutryListAdapter2);

    void inject(PaymentPage paymentPage);


    void inject(SessionManager sessionManager);

    void inject(ImageUtils imageUtils);

    void inject(Upcoming upcoming);

    void inject(AccountFragment accountFragment);

    void inject(HomeFragment homeFragment);

    void inject(Past past);

    void inject(RatingFragment ratingFragment);

    void inject(Comments comments);

    void inject(YourTrips yourTrips);

    void inject(TripDetails tripDetails);

    void inject(PaymentStatementActivity PaymentStatementActivity);

    void inject(EarningFragment earningFragment);

    void inject(MainActivity mainActivity);

    void inject(SigninSignupHomeActivity signinSignupHomeActivity);

    void inject(SplashActivity splashActivity);

    void inject(AddPayment addPayment);

    void inject(RiderProfilePage riderProfilePage);

    void inject(RequestReceiveActivity requestReceiveActivity);

    void inject(RequestAcceptActivity requestAcceptActivity);

    void inject(RiderContactActivity riderContactActivity);

    void inject(CancelYourTripActivity cancelYourTripActivity);

    void inject(PaymentAmountPage paymentAmountPage);

    void inject(PayStatementDetails payStatementDetails);

    void inject(TripEarningDetails payStatementDetails);

    void inject(DailyEarningDetails dailyEarningDetails);

    void inject(Riderrating riderrating);

    void inject(GpsService gps_service);

    void inject(RegisterCarDetailsActivity registerCarDetailsActivity);

    void inject(ResetPassword resetPassword);

    void inject(Register register);

    void inject(DocHomeActivity docHomeActivity);

    void inject(DocumentUploadActivity documentUploadActivity);



    void inject(RegisterOTPActivity registerOTPActivity);

    void inject(CommonMethods commonMethods);

    void inject(MobileActivity MobileActivity);

    void inject(SigninActivity signinActivity);

    void inject(RequestCallback requestCallback);

    void inject(RunTimePermission runTimePermission);

    void inject(DriverProfile driverProfile);

    void inject(VehiclInformation vehiclInformation);

    void inject(RiderFeedBack riderFeedBack);

    void inject(ActivityChat activityChat);

    void inject(FacebookAccountKitActivity facebookAccountKitActivity);



    // Adapters
    void inject(TripsAdapter tripsAdapter);

    void inject(CommentsRecycleAdapter commentsRecycleAdapter);

    void inject(LanguageAdapter languageAdapter);

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

    void inject(MyFirebaseInstanceIDService myFirebaseInstanceIDService);

    void inject(ImageCompressAsyncTask imageCompressAsyncTask);

    void inject (FirebaseChatHandler firebaseChatHandler);

    void inject (PayoutCountryListAdapter payoutCountryListAdapter);

    void inject (AdapterFirebaseRecylcerview adapterFirebaseRecylcerview);

//    service

    void inject (FirebaseChatNotificationService firebaseChatNotificationService);

    void inject (DownloadTask downloadTask);


    void inject (UpdateGPSWorker updateGPSWorker);

    void inject (LocationService locationService);

    void inject (AddFirebaseDatabase firebaseDatabase);


}
