package net.gntc.healing_and_blessing.audio;

import net.gntc.healing_and_blessing.viewmodel.SingleLiveEvent;

import androidx.lifecycle.MutableLiveData;

public interface AudioBinding {
    void onPrepared();
    void onPlayPause(boolean value);
    void onProgress(int position);
    void onCompleted();
    void onAmplitudeChanged(float radius);
}
