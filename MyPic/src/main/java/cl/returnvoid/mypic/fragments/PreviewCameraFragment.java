package cl.returnvoid.mypic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import cl.returnvoid.mypic.R;
import cl.returnvoid.mypic.util.PreviewCamera;

/**
 * Created by ggio on 22-07-13.
 */
public class PreviewCameraFragment extends Fragment {
    onShutterTrigger callback;
    protected PreviewCamera preview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preview = null;
    }

    public interface onShutterTrigger{

    }

    public PreviewCamera getPreview(){
        //yep
        preview = (PreviewCamera) getActivity().findViewById(R.id.preview);
        return preview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_preview_camera, container, false);
        return view;
    }
}