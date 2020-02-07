package net.gntc.healing_and_blessing.audio;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import net.gntc.healing_and_blessing.R;
import net.gntc.healing_and_blessing.room.AudioHistory;
import net.gntc.healing_and_blessing.room.HnB;
import net.gntc.healing_and_blessing.room.HnbRepository;
import net.gntc.healing_and_blessing.room.async.Command;
import net.gntc.healing_and_blessing.utils.DownloadUtil;
import net.gntc.healing_and_blessing.utils.FileUtil;
import net.gntc.healing_and_blessing.utils.NetworkUtil;
import net.gntc.healing_and_blessing.viewmodel.SingleLiveEvent;
import net.gntc.healing_and_blessing.viewmodel.ValidateObserver;
import net.gntc.healing_and_blessing.viewmodel.ValidationLiveData;

import java.io.IOException;

import androidx.lifecycle.Observer;

// 서비스 생성/제거 ,플레이어콜백 연결 , 플레이정보를 서비스로 전달

public class AudioServiceManager implements AudioBinding {
    Context _context;
    AudioService _service;
    ServiceConnection conn;

    ValidationLiveData<HnB> _source;
    SingleLiveEvent<Void> _call;
    SingleLiveEvent<Boolean> _callback;
    SingleLiveEvent<String> _networkError;


    Observer<Boolean> callbackObserver;
    ValidateObserver<HnB> srcObserver;

    private HnbRepository repository;
    private int _position = 0;
    private boolean _isDialogOpen = false;

    Command<Boolean> downloading;
    Command<Float> amplitudeCallback;
    Command<Void> onPrepared, onCompleted;
    Command<Boolean> onPlayPause;

    public AudioServiceManager(Context context) {
        this._context = context;
        repository = new HnbRepository((Application) _context);
    }

    public void bindService() {
        conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                AudioService.AsBinder binder = (AudioService.AsBinder) service;
                _service = binder.getService();
                _service.setViewBinding(_context, AudioServiceManager.this);
            }

            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent = new Intent(
                _context, // 현재 화면
                AudioService.class); // 다음넘어갈 컴퍼넌트

        _context.bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);
    }

    public void bindDownloadCallback(Command<Boolean> downloading) {
        this.downloading = downloading;
    }

    public void bindAmplitudeCallback(Command<Float> changed) {
        amplitudeCallback = changed;
    }

    // 소스 라이브 바인딩
    public void bindData(ValidationLiveData<HnB> source, SingleLiveEvent<Boolean> callback
            , SingleLiveEvent<Void> call, SingleLiveEvent<String> networkError) {

        this._source = source;
        this._callback = callback;
        this._call = call;
        this._networkError = networkError;
        srcObserver = new ValidateObserver<HnB>() {
            @Override
            public void onChanged(HnB item, HnB old) {
                String srcPath = repository.getAudioPath(item);
                if (old != null) {
                    if (!old.getPath().equals(item.getPath())) {
                        repository.updateItemHistoryState(old, 0);
                    }
                }
                repository.getHistroy(item.getPath())
                        .then(data -> {
                            AudioHistory history = (AudioHistory) data;
                            _position = history.getPosition();
                            if (history.getState() == 0) {
                                _call.call();
                                _isDialogOpen = true;
                            } else {
                                AudioServiceManager.this.setPlayer(srcPath, _position);
                            }
                            return true;
                        });
            }
        };

        callbackObserver = isContinue -> {
            String url = repository.getAudioPath(source.getValue());
            if (isContinue) {
                setPlayer(url, _position);
            } else {
                setPlayer(url, 0);
            }
            _isDialogOpen = false;
        };

        _source.observeForever(srcObserver);
        _callback.observeForever(callbackObserver);
    }

    public void bindMediaCallback(Command prepared, Command<Boolean> playPuse, Command completed) {
        onPrepared = prepared;
        onPlayPause = playPuse;
        onCompleted = completed;
    }

    public void setPlayer(String newName, int position) {
        try {
            if (!FileUtil.isExist(newName)) {
                if (NetworkUtil.getNetworkState(_context)) {
                    String key = newName.replace(_context.getFilesDir().getAbsolutePath() + "/", "");
                    String server = String.format(_context.getResources().getString(R.string.audio), key);
                    new DownloadUtil().DownloadAsync(
                            server,
                            newName,
                            downloading,
                            (Command<Boolean>) result -> {
                                if (result) {       // 다운로드 성공시 콜백
                                    try {
                                        _service.setPlayer(newName, position);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    _networkError.setValue(_context.getString(R.string.download_failed));
                                    FileUtil.removeFile(newName);
                                    //다운로드 실패 예외처리
                                }
                            });
                } else {
                    _networkError.setValue(_context.getString(R.string.network_disconneted));
                }

            } else {
                _service.setPlayer(newName, position);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared() {
        if (onPrepared != null)
            onPrepared.execute(null);
    }

    @Override
    public void onPlayPause(boolean value) {
        if (onPlayPause != null)
            onPlayPause.execute(value);
    }

    @Override
    public void onProgress(int position) {
        //기록
        repository.setHisotry(
                Integer.parseInt(_source.getValue().getGubun()),
                _source.getValue().getPath(),
                position,
                1);
    }

    @Override
    public void onCompleted() {
        repository.clearHistory(Integer.parseInt(_source.getValue().getGubun()));

        if (onCompleted != null)
            onCompleted.execute(null);
    }

    @Override
    public void onAmplitudeChanged(float radius) {
        if (!_isDialogOpen) {
            amplitudeCallback.execute(radius);
        }
    }

    public void onResume() {
        if (_service != null) {
            _service.onResume();
        }
    }

    public void unbindService() {
        _service.dispose();
        _source.removeAllObserver();
        _callback.removeObserver(callbackObserver);
        _context.unbindService(conn);

        if (!_service.isCompleted()) {  //  다음 실행시 세이브포인트
            repository.updateHistoryState(0);
        }
    }
}
