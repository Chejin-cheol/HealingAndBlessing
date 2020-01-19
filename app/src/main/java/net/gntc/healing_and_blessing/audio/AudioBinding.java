package net.gntc.healing_and_blessing.audio;

import net.gntc.healing_and_blessing.viewmodel.SingleLiveEvent;

import androidx.lifecycle.MutableLiveData;

public abstract class AudioBinding {
    public boolean audioPermission;
    abstract void onPreprocess();
    abstract void onPrepared();
    abstract void onProgress(int position);
    abstract void onCompleted();
    abstract void onAmplitudeChanged(float radius);
}
