package net.gntc.healing_and_blessing.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import net.gntc.healing_and_blessing.R;
import net.gntc.healing_and_blessing.databinding.ActivitySettingBinding;
import net.gntc.healing_and_blessing.file.ResourceManager;
import net.gntc.healing_and_blessing.utils.FileUtil;
import net.gntc.healing_and_blessing.utils.PreferenceUtil;
import net.gntc.healing_and_blessing.viewmodel.SettingVIewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class SettingActivity extends AppCompatActivity {
    final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    SettingVIewModel vIewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!PreferenceUtil.getBoolean(this, "init")) {
            ResourceManager resourceManager = new ResourceManager(this);
            resourceManager.decompressFromAssets("data.zip", "data");
        }

        ActivitySettingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setLifecycleOwner(this);
        vIewModel = ViewModelProviders.of(this).get(SettingVIewModel.class);
        binding.setViewModel(vIewModel);

        setPermission();
    }


    public void setPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        else{
            intent();
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
                        intent();
                    }
                }
                else{
                    PreferenceUtil.setBoolean(this, Manifest.permission.RECORD_AUDIO ,true);intent();
                    intent();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    void intent(){
        Intent intent = new Intent(this , MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
