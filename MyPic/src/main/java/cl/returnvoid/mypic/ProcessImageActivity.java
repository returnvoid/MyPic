package cl.returnvoid.mypic;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import java.io.File;
import java.net.URI;

public class ProcessImageActivity extends Activity {
    public static final String PROCESS_IMAGE_ACTIVITY = "PROCESS_IMAGE_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_process_image);

        String uri = getIntent().getStringExtra("imaged_saved_uri");
        Log.d(PROCESS_IMAGE_ACTIVITY, "uri: " + uri);

        ImageView imageView = (ImageView)findViewById(R.id.image);
        File file = new File(uri);
        imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.process_image, menu);
        return true;
    }
    
}
