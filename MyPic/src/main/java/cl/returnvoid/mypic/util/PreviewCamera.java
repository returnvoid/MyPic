package cl.returnvoid.mypic.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ggio on 20-07-13.
 */
public class PreviewCamera extends SurfaceView implements SurfaceHolder.Callback{
    public static final String PREVIEW_CAMERA = "PREVIEW_CAMERA";
    private Camera camera;
    private Boolean cameraConfigured = false;
    public Uri imageSaved;
    public Intent imageIntent;
    public PreviewCamera(Context context, AttributeSet attrs){
        super(context, attrs);
        //setWillNotDraw(false);
        imageSaved = Uri.EMPTY;
        getHolder().addCallback(this);
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.d(PREVIEW_CAMERA, "On Draw Called");
    }

    public void previewAsBitmap(Intent imageIntent){
        this.imageIntent = imageIntent;
        if(camera!=null&&getHolder().getSurface()!=null){
            camera.takePicture(shutterCallback, pictureCallback, jpegCallBack);
        }
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            MediaPlayer shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            shootMP.start();
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

            String photoName = "mypic_" + number + String.format("_%d", System.currentTimeMillis()) + ".jpg";
            File photo = new File(folder, photoName);

            imageSaved = Uri.fromFile(photo);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageSaved);

            try {
                FileOutputStream out = new FileOutputStream(photo.getPath());
                Bitmap bmf = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                int w = 612;
                int h = 612;
                int width = bmf.getWidth();
                int height = bmf.getHeight();
                float scaleWidth = ((float) w) / width;
                float scaleHeight = ((float) h) / height;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap mutableBm = bmf.createBitmap(bmf, 0, 0, width, height, matrix, true);

                Canvas canvas = new Canvas(mutableBm);
                Typeface type = Typeface.create("Roboto", Typeface.BOLD);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.WHITE);
                paint.setTypeface(type);
                paint.setTextSize(60);
                canvas.drawBitmap(mutableBm, 0, 0, paint);
                canvas.drawText("#Hinchagram", 60, h - 60, paint);

                mutableBm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                Log.d(PREVIEW_CAMERA, "imageSaved: " + imageSaved.toString());
            }
            catch (java.io.IOException e) {
                Log.e(PREVIEW_CAMERA, "Exception in photoCallback", e);
            }
            MediaScannerConnection.scanFile(getContext(),
                new String[]{photo.getPath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                }
            );
            reactivateCamera();
        }
    };

    public void reactivateCamera(){
        camera.startPreview();
    }


    private void initPreview(int width, int height) {
        if (camera!=null && getHolder().getSurface()!=null) {
            try {
                camera.setPreviewDisplay(getHolder());
            }
            catch (Throwable t) {
                Log.d(PREVIEW_CAMERA, t.toString());
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
        Log.d(PREVIEW_CAMERA, "startPreview auto");
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