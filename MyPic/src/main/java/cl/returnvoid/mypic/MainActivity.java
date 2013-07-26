package cl.returnvoid.mypic;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cl.returnvoid.mypic.fragments.PreviewCameraFragment;


public class MainActivity extends FragmentActivity {
    private String ACTIVITY_TAG = "ACTIVITY_TAG";
    public PreviewCameraFragment previewCameraFragment;
    public Intent imageIntent;
    protected Button shutterButton;
    protected LinearLayout previewCameraContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this is main activity
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        previewCameraContainer = (LinearLayout) findViewById(R.id.preview_camera_container);

        shutterButton = (Button)findViewById(R.id.shutter_button);
        shutterButton.setVisibility(View.INVISIBLE);

        Display display = getWindowManager().getDefaultDisplay();
        previewCameraContainer.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth(), display.getWidth() + 20));

        previewCameraFragment = new PreviewCameraFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.preview_camera_container, previewCameraFragment)
                .commit();
        previewCameraContainer.setVisibility(View.INVISIBLE);

        Button initAppButton = (Button) findViewById(R.id.init_app_button);
        initAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                btn.setVisibility(View.GONE);
                showPreviewCameraContainer();
            }
        });
    }

    private void showPreviewCameraContainer(){
        shutterButton.setVisibility(View.VISIBLE);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCameraFragment.getPreview().capturePreviewCamera();
            }
        });
        previewCameraContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //previewCameraFragment.getPreview().openCamera();
    }

    @Override
    public void onPause(){
        super.onPause();
        //previewCameraFragment.getPreview().releaseCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent prefs = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(prefs);
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
