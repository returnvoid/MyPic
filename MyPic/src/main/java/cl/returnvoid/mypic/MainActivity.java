package cl.returnvoid.mypic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import cl.returnvoid.mypic.fragments.PreviewCameraFragment;


public class MainActivity extends FragmentActivity {
    private String ACTIVITY_TAG = "ACTIVITY_TAG";
    private Boolean shuterStatus = false;
    public PreviewCameraFragment previewCameraFragment;
    private Animation translate;
    public Intent imageIntent;
    private RelativeLayout.LayoutParams params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this is main activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewCameraFragment = new PreviewCameraFragment();

        Log.d(ACTIVITY_TAG, "onCreate");

        Button shutter = (Button) findViewById(R.id.shutter);
        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Button btn = (Button) view;
                try {
                    translate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animate_shutter_button);
                    if (translate != null) {
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                params = (RelativeLayout.LayoutParams) btn.getLayoutParams();
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                shuterStatus = true;
                                //params.topMargin = 200 + params.topMargin;
                                //btn.setLayoutParams(params);
                                btn.setVisibility(View.GONE);

                                getSupportFragmentManager().beginTransaction().add(R.id.preview_camera_container, previewCameraFragment).commit();
                                //preview.setVisibility(View.VISIBLE);
                                Log.d(ACTIVITY_TAG, "animationEnded");
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }catch (Exception e){
                    Log.d(ACTIVITY_TAG, "ended");
                }
                if(!shuterStatus){
                    Log.d(ACTIVITY_TAG, "startAnimation");
                    view.startAnimation(translate);
                }else{
                    /*imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    previewCameraFragment.getPreview().previewAsBitmap(imageIntent);*/
                    //f.getPreview().previewAsBitmap(imageIntent);

                    //Log.d(ACTIVITY_TAG, "picture saved:" + previewCameraFragment.getPreview().imageSaved);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
