package net.gntc.healing_and_blessing.viewmodel;

import android.app.Application;
import android.util.Log;

import net.gntc.healing_and_blessing.audio.AudioServiceManager;
import net.gntc.healing_and_blessing.room.HnB;
import net.gntc.healing_and_blessing.room.HnbRepository;
import net.gntc.healing_and_blessing.room.async.Command;
import net.gntc.healing_and_blessing.room.async.Promise;
import net.gntc.healing_and_blessing.utils.PreferenceUtil;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static net.gntc.healing_and_blessing.room.HnB.HEALING;

public class MainViewModel extends AndroidViewModel {

    Application _application;
    AudioServiceManager serviceManager = null;
    HnbRepository repository;

    //view data
    public MutableLiveData<Float> healRadius = new MutableLiveData<Float>();
    public MutableLiveData<Float> blessingRadius = new MutableLiveData<Float>();
    public ValidationLiveData<HnB> source = new ValidationLiveData<HnB>();

    private SingleLiveEvent<Boolean> isLoading = new SingleLiveEvent<Boolean>();
    public SingleLiveEvent<Boolean> getIsLoading() {return isLoading;}

    // 다이아로그 콜백
    private SingleLiveEvent<Boolean> dialogCallback = new SingleLiveEvent<Boolean>();
    public SingleLiveEvent<Boolean> getdialogCallback() {return dialogCallback;}

    //다이아로그 호출
    private SingleLiveEvent<Void> dialogCall = new SingleLiveEvent<Void>();
    public SingleLiveEvent<Void> getDialogCall() {
        return dialogCall;
    }

    // 네트워크에러
    SingleLiveEvent<String> networkError = new SingleLiveEvent<String>();
    public SingleLiveEvent<String>  getNetworkError(){return networkError;}

    public MainViewModel(@NonNull Application application) {
        super(application);
        _application = application;
        isLoading.setValue(false);

        if (!PreferenceUtil.getBoolean(application, "init")) {
            repository = new HnbRepository(application);
            PreferenceUtil.setBoolean(application, "init", true);
        } else {
            repository = new HnbRepository(application);
        }
        setAudioService();

    }

    public void setAudioService() {
        serviceManager = new AudioServiceManager(getApplication());
        serviceManager.bindService();
        serviceManager.bindData(source, dialogCallback ,dialogCall ,networkError);
        serviceManager.bindDownloadCallback(downloading ->{
            isLoading.setValue(downloading);
        });
        serviceManager.bindAmplitudeCallback(new Command<Float>() {
            @Override
            public void execute(Float f) {
                int key = Integer.parseInt(source.getValue().getGubun());
                if(key == HEALING){
                    healRadius.setValue(f);
                }
                else{
                    blessingRadius.setValue(f);
                }
            }
        });
    }

    public void onClick(int gubun) {
        repository.getCount(gubun)      // select count 질의
                .then((count) -> {
                    Promise p = new Promise();
                    int days = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                    int max = (int) count;
                    int seq = 0;

                    if (gubun == HEALING) {
                        seq = days < max ? (max % days) : (days % max);
                    } else {
                        seq = days % max;
                    }
                    seq++;
                    p.resolve(seq);
                    return p;
                })
                .then((seq) -> repository.getItem((int) seq, gubun))    // 데이터 질의
                .then((item) -> {
                    source.setValue((HnB) item);
                    return true;
                });
    }
    public void dispose() {
        serviceManager.unbindService();
    }

    public void onResume() {
        serviceManager.onResume();
    }
}
