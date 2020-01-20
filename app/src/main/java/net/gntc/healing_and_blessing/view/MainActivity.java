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

    public final String healing_path = "shinyu";
    public final String blessing_path = "10min";
    final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    HistoryDialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPermission();

        if (!PreferenceUtil.getBoolean(this, "init")) {
            ResourceManager resourceManager = new ResourceManager(this);
            resourceManager.decompressFromAssets("data.zip", "data");
            FileUtil.dirChecker(this.getFilesDir() + "/" + blessing_path);  //없으면 생성
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        setLiveEvent();

        dialog = HistoryDialogFragment.newInstance();
        dialog.setEvent(this
                , viewModel.getDialogCall()
                , viewModel.getdialogCallback());
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

    public void setPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO){
            for(int i=0; i < permissions.length ; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[i]))
                    {
                        setPermission();
                    }
                    else{
                        PreferenceUtil.setBoolean(this, Manifest.permission.RECORD_AUDIO ,false);
                    }
                }
                else{
                    PreferenceUtil.setBoolean(this, Manifest.permission.RECORD_AUDIO ,true);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
