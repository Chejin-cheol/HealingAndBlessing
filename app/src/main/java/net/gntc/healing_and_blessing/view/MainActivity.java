package net.gntc.healing_and_blessing.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import net.gntc.healing_and_blessing.R;
import net.gntc.healing_and_blessing.databinding.ActivityMainBinding;
import net.gntc.healing_and_blessing.file.ResourceManager;
import net.gntc.healing_and_blessing.utils.FileUtil;
import net.gntc.healing_and_blessing.utils.PreferenceUtil;
import net.gntc.healing_and_blessing.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    MainViewModel viewModel;
    ActivityMainBinding binding;

    HistoryDialogFragment dialog;
    ProgressFragment progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.church.setClipToOutline(true);
        setLiveEvent();

        dialog = HistoryDialogFragment.newInstance();
        dialog.setEvent(this
                , viewModel.getDialogCall()
                , viewModel.getdialogCallback());
        dialog.setCancelable(false);

        progress = ProgressFragment.newInstance();
        progress.setEvent(this
                , viewModel.getIsLoading());
        dialog.setCancelable(false);

    }

    public void setLiveEvent(){
        viewModel.getNetworkError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                Toast.makeText(MainActivity.this , message , Toast.LENGTH_SHORT ).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
    }


    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    public  void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this,
                    getString(R.string.back_press_alert), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            this.finish();
            toast.cancel();
        }
    }
}
