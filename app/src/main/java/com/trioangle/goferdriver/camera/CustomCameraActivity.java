package com.trioangle.goferdriver.camera;
/**
 * @package com.trioangle.goferdriver
 * @subpackage Camera
 * @category CustomCameraActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.PreviewActivity;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.internal.enums.MediaAction;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultListener;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentVideoRecordTextAdapter;
import com.github.florent37.camerafragment.widgets.CameraSettingsView;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.MediaActionSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;
import com.trioangle.goferdriver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* ************************************************************
                      CustomCameraActivity
    Customized camera for upload and images using camera
*************************************************************** */
public class CustomCameraActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;
    private final static String MEDIA_ACTION_ARG = "media_action_arg";
    private final static String FILE_PATH_ARG = "file_path_arg";
    private CameraSettingsView settingsView;
    private FlashSwitchView flashSwitchView;
    private CameraSwitchView cameraSwitchView;
    private RecordButton recordButton;
    private MediaActionSwitchView mediaActionSwitchView;
    private TextView recordDurationText;
    private TextView recordSizeText;
    private RelativeLayout cameraLayout;
    private Button addCameraButton;

    /*
    *  Intent for take photo
    **/
    public static Intent newIntentPhoto(Context context, String filePath) {
        return new Intent(context, CustomCameraActivity.class)
                .putExtra(MEDIA_ACTION_ARG, MediaAction.ACTION_PHOTO)
                .putExtra(FILE_PATH_ARG, filePath);
    }

    /*
    *  Intent for take video
    **/
    public static Intent newIntentVideo(Context context, String filePath) {
        return new Intent(context, CustomCameraActivity.class)
                .putExtra(MEDIA_ACTION_ARG, MediaAction.ACTION_VIDEO)
                .putExtra(FILE_PATH_ARG, filePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);

        settingsView = (CameraSettingsView) findViewById(R.id.settings_view);
        flashSwitchView = (FlashSwitchView) findViewById(R.id.flash_switch_view);
        cameraSwitchView = (CameraSwitchView) findViewById(R.id.front_back_camera_switcher);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        mediaActionSwitchView = (MediaActionSwitchView) findViewById(R.id.photo_video_camera_switcher);
        recordDurationText = (TextView) findViewById(R.id.record_duration_text);
        recordSizeText = (TextView) findViewById(R.id.record_size_mb_text);
        addCameraButton = (Button) findViewById(R.id.addCameraButton);
        cameraLayout = (RelativeLayout) findViewById(R.id.cameraLayout);

    /*
    *  Request permission
    **/
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else addCamera();
        } else {
            addCamera();
        }

        settingsView.setOnClickListener(this);
        flashSwitchView.setOnClickListener(this);
        cameraSwitchView.setOnClickListener(this);
        recordButton.setOnClickListener(this);
        mediaActionSwitchView.setOnClickListener(this);
    }

    /*
    *   Flash on / off
    **/
    public void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    /*
    *  Switch to camera front and back
    **/
    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }

    /*
    *  Take photo or record video
    **/
    public void onRecordButtonClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                @Override
                public void onVideoRecorded(String filePath) {
                    Toast.makeText(getBaseContext(), "onVideoRecorded " + filePath, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                    Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();

                }
            }, null, "photo0");
        }

        // "/storage/self/primary",
        //  "photo0"
    }

    /*
     *  Activity result for get data from camera
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            /*Intent resultIntent = new Intent();
            if (view.getId() == com.github.florent37.camerafragment.R.id.confirm_media_result) {
                resultIntent.putExtra(RESPONSE_CODE_ARG, ACTION_CONFIRM).putExtra(FILE_PATH_ARG, previewFilePath);
            } else if (view.getId() == com.github.florent37.camerafragment.R.id.re_take_media) {
                deleteMediaFile();
                resultIntent.putExtra(RESPONSE_CODE_ARG, ACTION_RETAKE);
            } else if (view.getId() == com.github.florent37.camerafragment.R.id.cancel_media_action) {
                deleteMediaFile();
                resultIntent.putExtra(RESPONSE_CODE_ARG, ACTION_CANCEL);
            }*/
        setResult(RESULT_OK, data);
        finish();
    }

    /*
     *  Open setting page
     **/
    public void onSettingsClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.openSettingDialog();
        }
    }

    /*
    *  Switch photo and video
    **/
    public void onMediaActionSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchActionPhotoVideo();
        }
    }


    /*
    *  Permission for camera
   **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            addCamera();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {
        addCameraButton.setVisibility(View.GONE);
        cameraLayout.setVisibility(View.VISIBLE);

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {
            cameraFragment.setResultListener(new CameraFragmentResultListener() {
                @Override
                public void onVideoRecorded(String filePath) {
                    Intent intent = PreviewActivity.newIntentVideo(CustomCameraActivity.this, filePath);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                    Intent intent = PreviewActivity.newIntentPhoto(CustomCameraActivity.this, filePath);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            });

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                    cameraSwitchView.displayBackCamera();
                }

                @Override
                public void onCurrentCameraFront() {
                    cameraSwitchView.displayFrontCamera();
                }

                @Override
                public void onFlashAuto() {
                    flashSwitchView.displayFlashAuto();
                }

                @Override
                public void onFlashOn() {
                    flashSwitchView.displayFlashOn();
                }

                @Override
                public void onFlashOff() {
                    flashSwitchView.displayFlashOff();
                }

                @Override
                public void onCameraSetupForPhoto() {
                    mediaActionSwitchView.displayActionWillSwitchVideo();

                    recordButton.displayPhotoState();
                    flashSwitchView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCameraSetupForVideo() {
                    mediaActionSwitchView.displayActionWillSwitchPhoto();

                    recordButton.displayVideoRecordStateReady();
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    ViewCompat.setRotation(cameraSwitchView, degrees);
                    ViewCompat.setRotation(mediaActionSwitchView, degrees);
                    ViewCompat.setRotation(flashSwitchView, degrees);
                    ViewCompat.setRotation(recordDurationText, degrees);
                    ViewCompat.setRotation(recordSizeText, degrees);
                }

                @Override
                public void onRecordStateVideoReadyForRecord() {
                    recordButton.displayVideoRecordStateReady();
                }

                @Override
                public void onRecordStateVideoInProgress() {
                    recordButton.displayVideoRecordStateInProgress();
                }

                @Override
                public void onRecordStatePhoto() {
                    recordButton.displayPhotoState();
                }

                @Override
                public void onStopVideoRecord() {
                    recordSizeText.setVisibility(View.GONE);
                    //cameraSwitchView.setVisibility(View.VISIBLE);
                    settingsView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStartVideoRecord(File outputFile) {

                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    cameraSwitchView.setEnabled(false);
                    recordButton.setEnabled(false);
                    settingsView.setEnabled(false);
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    cameraSwitchView.setEnabled(true);
                    recordButton.setEnabled(true);
                    settingsView.setEnabled(true);
                    flashSwitchView.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                    cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
                    mediaActionSwitchView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }
            });

            cameraFragment.setTextListener(new CameraFragmentVideoRecordTextAdapter() {
                @Override
                public void setRecordSizeText(long size, String text) {
                    recordSizeText.setText(text);
                }

                @Override
                public void setRecordSizeTextVisible(boolean visible) {
                    recordSizeText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

                @Override
                public void setRecordDurationText(String text) {
                    recordDurationText.setText(text);
                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                    recordDurationText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    /*
     *  Fragement for camera
     **/
    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash_switch_view:
                onFlashSwitcClicked();
                break;
            case R.id.photo_video_camera_switcher:
                onMediaActionSwitchClicked();
                break;
            case R.id.settings_view:
                onSettingsClicked();
                break;
            case R.id.record_button:
                onRecordButtonClicked();
                break;
            case R.id.front_back_camera_switcher:
                onSwitchCameraClicked();
                break;
            default:
                break;
        }
    }
}
