package info.culebrasgis.linternapez;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

    private ImageButton fishButton;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    private Parameters params;
    private MediaPlayer mp;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();

        fishButton = (ImageButton) findViewById(R.id.imageButton);

        // Check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // Device doesn't support flash
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Error");
            alert.setMessage(res.getString(R.string.text_not_supported));
            alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Close the app
                    finish();
                }
            });
            alert.show();
            return;
        }

        // ImageButton click event to toggle flash on/off
        fishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    turnOffFlash();
                } else {
                    turnOnFlash();
                }
            }
        });

        // Load ads
        showAds();
    }

    // Toggle button images
    private void toggleButtonImage(){
        if(isFlashOn){
            fishButton.setImageResource(R.drawable.fish_on);
        }else{
            fishButton.setImageResource(R.drawable.fish_off);
        }
    }

    // Playing sound on flash on/off
    private void playSound(){
        if(isFlashOn){
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
        }else{
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            toggleButtonImage();

            playSound();
        }
    }

    // Turning Off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            toggleButtonImage();

            playSound();
        }
    }

    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.error_camera),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // On starting get the camera parameters
        getCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // On destroy release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    // Find AdView as resource and load a request
    private void showAds() {
        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            AlertDialog.Builder altDialog = new AlertDialog.Builder(MainActivity.this);
            altDialog.setMessage(R.string.text_help);
            altDialog.setNeutralButton("OK", null);
            altDialog.show();
            return true;
        }
        if (id == R.id.action_about) {
            AlertDialog.Builder altDialog = new AlertDialog.Builder(MainActivity.this);
            altDialog.setMessage(R.string.text_about);
            altDialog.setNeutralButton("OK", null);
            altDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
