package net.gntc.healing_and_blessing.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gntc.healing_and_blessing.R;
import net.gntc.healing_and_blessing.room.async.Command;
import net.gntc.healing_and_blessing.viewmodel.SingleLiveEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class HistoryDialogFragment extends DialogFragment implements View.OnClickListener {

    private Command<Boolean> callbackCommand;

    public static HistoryDialogFragment newInstance() {
        HistoryDialogFragment fragment = new HistoryDialogFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.history_check_dialog, container);
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int width = (int) (metrics.widthPixels * 0.85);
        int height = (int) (metrics.heightPixels * 0.3);
        int padding = ((int)(width *0.03));

        ConstraintLayout layer = (ConstraintLayout)v.findViewById(R.id.layer);
        ConstraintLayout content = (ConstraintLayout)v.findViewById(R.id.content);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width , height);
        layer.setLayoutParams(params);
        layer.setPadding( padding ,padding ,padding ,padding );
        content.setPadding( padding ,padding ,padding ,padding );

        v.findViewById(R.id.dialog_confirm_btn).setOnClickListener(this);
        v.findViewById(R.id.dialog_close_btn).setOnClickListener(this);

        return v;
    }

    public void setEvent(LifecycleOwner owner , SingleLiveEvent<Void> call , SingleLiveEvent<Boolean> callback ){
        call.observe(owner , visibility -> {
            if(! isAdded() ){
                show(((AppCompatActivity) owner).getSupportFragmentManager(), "DIALOG");
            }
        });
        callbackCommand = aBoolean -> callback.setValue(aBoolean);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.dialog_confirm_btn){
            callbackCommand.execute(true);
        }
        else{
            callbackCommand.execute(false);
        }
        dismiss();
    }
}