package net.gntc.healing_and_blessing.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gntc.healing_and_blessing.R;
import net.gntc.healing_and_blessing.viewmodel.SingleLiveEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

public class ProgressFragment extends DialogFragment {

    public static ProgressFragment newInstance() {
        ProgressFragment fragment = new ProgressFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.progress_fragment, container);
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int width = (int) (metrics.widthPixels * 0.3);
        ConstraintLayout layer = (ConstraintLayout)v.findViewById(R.id.layer);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width , width);
        layer.setLayoutParams(params);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
            {
                //Hide your keyboard here!!!
                return true; // pretend we've processed it
            }
            else
                return false; // pass on to be processed as normal
        });
    }

    public void setEvent(LifecycleOwner owner , SingleLiveEvent<Boolean> callback ){
        callback.observe(owner , visibility -> {
            if(visibility) {
                if (!isAdded()) {
                    show(((AppCompatActivity) owner).getSupportFragmentManager(), "DIALOG");
                }
            }
            else {
                dismiss();
            }
        });
    }

}
