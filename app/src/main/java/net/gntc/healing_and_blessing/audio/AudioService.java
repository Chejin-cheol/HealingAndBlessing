package net.gntc.healing_and_blessing.audio;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import net.gntc.healing_and_blessing.utils.FileUtil;
import net.gntc.healing_and_blessing.utils.PreferenceUtil;
import net.gntc.healing_and_blessing.utils.ScreenUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private final static int TIME_PERIOD = 1000;
    private final IBinder mBinder = new AsBinder();
    private AudioBinding _binding;
    private Context _context;

    private boolean auioRecordAvailable;
    private String _url = "";
    private boolean isCompleted = false;
    private int _checkPoint = 0;

    private MediaPlayer _player;
    private Timer _timer = new Timer();
    private TimerTask _task;
    private AudioManager audioManager;
    private AudioCapture _audioCapture;
    private Handler visualHandler;

    public class AsBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        visualHandler = new Handler();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void setViewBinding(Context context, AudioBinding vb) {
        _context = context;
        _binding = vb;
        auioRecordAvailable = PreferenceUtil.getBoolean(_context, Manifest.permission.RECORD_AUDIO);
        setAudioFocus();
    }

    public void setPlayer(String url, int position) throws IOException {
        _checkPoint = position;
        if (!_url.equals(url)) {
            if (_player == null) {
                play(url);
            } else {
                visualHandler.removeCallbacksAndMessages(null);

                setTimerState(false);
                releaseMP();
                play(url);
            }
            _url = url;
        } else {
            if (!isCompleted) {
                if (_player.isPlaying()) {
                    _player.pause();
                } else {
                    _player.start();
                }
            }
        }
    }

    private void setAudioFocus() {
        audioManager = (AudioManager) (_context).getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    private void play(String url) {
        try {
            _player = new MediaPlayer();
            _player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            _player.setDataSource(url);
            _player.setOnPreparedListener(this);
            _player.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseMP() {
        if (_player != null) {
            try {
                _player.release();
                _player = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getAudioOutputAmplitude(double durationInSeconds) throws InterruptedException {
        int minValue = 0;
        int maxValue = 0;
        if (_player != null) {
            _audioCapture = new AudioCapture(AudioCapture.TYPE_PCM, 1024, _player.getAudioSessionId());
            _audioCapture.start();
            Thread.sleep((long) (durationInSeconds * 1000));
            int[] mVizData;
            mVizData = _audioCapture.getFormattedData(1, 1);
            _audioCapture.release();

            for (int value : mVizData) {
                Log.i("진폭", (value) + "");
                if (value < minValue) {
                    minValue = value;
                } else if (value > maxValue) {
                    maxValue = value;
                }
            }
        }
        return maxValue - minValue;
    }

    // audio interface
    @Override
    public void onPrepared(MediaPlayer mp) {
//        _binding.onPrepared();
        isCompleted = false;
        _player.seekTo(_checkPoint);
        mp.start();
        setTimerState(true);

        if (auioRecordAvailable) {
            visualHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int maxAmplitude = getAudioOutputAmplitude(0.3);
                        float radius = (float) Math.log10(Math.max(1, maxAmplitude)) * ScreenUtil.dp2px(_context, 60);
                        _binding.onAmplitudeChanged(radius);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!isCompleted) {
                        visualHandler.postDelayed(this, 300);
                    }
                }
            });
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isCompleted = true;
        _binding.onCompleted();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.i("포커스", focusChange + "");
        switch (focusChange) {
            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                break;
            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                Log.i("살짝 잃음", "잃음");
                _player.pause();
                break;

            case (AudioManager.AUDIOFOCUS_LOSS):
                Log.i("아주잃음", "잃음");
                _player.pause();
                break;

            case (AudioManager.AUDIOFOCUS_GAIN):
                Log.i("얻음", "얻음");
                // Return the volume to normal and resume if paused.
                _player.start();
                break;
            default:
                break;
        }
    }


    // Focuse lose exception
    public void onResume() {
        if (_player != null && !isCompleted) {
            if (!_player.isPlaying()) {
                _player.start();
                setAudioFocus();
            }
        }
    }

    // timer정의 :room 디비로 데이터 전송
    public void setTimerState(boolean state) {
        if (state) {
            _task = new TimerTask() {
                @Override
                public void run() {
                    if (_player != null) {
                        if (_player.isPlaying()) {
                            _binding.onProgress(_player.getCurrentPosition());
                        }
                    }
                }
            };
            //타이머
            _timer.schedule(_task, 0, TIME_PERIOD);
        } else {
            if (_task != null) {
                _task.cancel();
                _task = null;
            }
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void dispose() {
        setTimerState(false);
        _timer.cancel();
        _timer = null;
        releaseMP();
        audioManager.abandonAudioFocus(this);
    }
}
