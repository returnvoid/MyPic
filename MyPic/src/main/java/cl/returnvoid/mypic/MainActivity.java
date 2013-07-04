package cl.returnvoid.mypic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;

public class MainActivity extends Activity{
    private String ACTIVITY_TAG = "ACTIVITY_TAG";
    final static int TAKE_PICTURE_WITH_CAMERA = 0;
    private Boolean shuterStatus = false;
    private Boolean cameraConfigured = false;
    private SurfaceView preview;
    private SurfaceHolder holder;
    private Camera camera;
    private Animation translate;
    private File path;
    private RelativeLayout.LayoutParams params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this is main activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = (SurfaceView) findViewById(R.id.preview);
        preview.setMinimumHeight(preview.getWidth());
        holder = preview.getHolder();
        holder.addCallback(surfaceCallback);
        preview.setVisibility(View.GONE);

        Log.d(ACTIVITY_TAG, "onCreate");

        Button shutter = (Button) findViewById(R.id.shutter);
        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                                params.topMargin = 200 + params.topMargin;
                                btn.setLayoutParams(params);
                                preview.setVisibility(View.VISIBLE);
                                camera = Camera.open();
                                startPreview();
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
                    Log.d(ACTIVITY_TAG, "saving");
                    //take photo
                    Intent save = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyPic");
                    if(!path.exists()){
                        Log.d(ACTIVITY_TAG, "no existe"+path.toString());
                        path.mkdir();
                    }
                    //capture image from surface view
                    Bitmap bm = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                    Canvas cv = new Canvas(bm);
                    preview.draw(cv);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result=null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                }
                else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private void initPreview(int width, int height) {
        if (camera!=null && holder.getSurface()!=null) {
            try {
                camera.setPreviewDisplay(holder);
            }
            catch (Throwable t) {
                Log.d("T", t.toString());
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=camera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height, parameters);

                if (size!=null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            //inPreview=true;
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            initPreview(width, height);
            Log.d("Callback", "startPreview auto");
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };
    
}
