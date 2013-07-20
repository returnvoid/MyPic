package cl.returnvoid.mypic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

class PreviewCamera extends SurfaceView implements SurfaceHolder.Callback{
    public static final String PREVIEW_CAMERA = "PREVIEW_CAMERA";
    private Camera camera;
    private Boolean cameraConfigured = false;
    public PreviewCamera(Context context, AttributeSet attrs){
        super(context, attrs);
        //setWillNotDraw(false);
        getHolder().addCallback(this);
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.w(this.getClass().getName(), "On Draw Called");
    }

    public void previewAsBitmap(){
        if(camera!=null&&getHolder().getSurface()!=null){
            camera.takePicture(shutterCallback, pictureCallback, jpegCallBack);
        }

    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            MediaPlayer _shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            _shootMP.start();
        }
    };

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    Camera.PictureCallback jpegCallBack = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyPic");
            if(!folder.exists()){
                folder.mkdir();
            }
            int number = folder.listFiles().length;;

            String photoName = "mypic" + number + ".jpg";
            File photo = new File(folder, photoName);

            Uri imageSaved = Uri.fromFile(photo);
            Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageSaved);
            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());
                fos.write(bytes);
                fos.close();
            }
            catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{photo.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d(PREVIEW_CAMERA, "Scanned " + path + ":");
                            Log.d(PREVIEW_CAMERA, "-> uri=" + uri);
                        }
                    }
            );
            camera.startPreview();
        }
    };


    private void initPreview(int width, int height) {
        if (camera!=null && getHolder().getSurface()!=null) {
            try {
                camera.setPreviewDisplay(getHolder());
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

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        initPreview(width, height);
        Log.d("Callback", "startPreview auto");
        startPreview();
    }

    public void surfaceCreated(SurfaceHolder holder){
        camera = Camera.open();
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        camera.stopPreview();
        camera = null;
    }
}
public class MainActivity extends Activity{
    private String ACTIVITY_TAG = "ACTIVITY_TAG";
    private Boolean shuterStatus = false;
    private PreviewCamera preview;
    private Animation translate;
    private RelativeLayout.LayoutParams params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this is main activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = (PreviewCamera) findViewById(R.id.preview);
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
                    //capture image from surface view
                    preview.previewAsBitmap();
                    Log.d(ACTIVITY_TAG, "picture saved");
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
