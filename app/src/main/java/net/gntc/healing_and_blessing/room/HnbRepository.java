package net.gntc.healing_and_blessing.room;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import net.gntc.healing_and_blessing.room.async.Promise;
import net.gntc.healing_and_blessing.utils.DownloadUtil;
import net.gntc.healing_and_blessing.utils.FileUtil;

import androidx.lifecycle.Observer;

import static net.gntc.healing_and_blessing.room.HnB.HEALING;

public class HnbRepository {

    public static final int QUERY_COUNT = 3;
    public static final int QUERY_ITEM = 4;

    DataDao dao;
    Application application;

    public HnbRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.dataDao();
    }

    public Promise getCount(int gubun) {
        Promise p = new Promise();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int count = dao.getCount(gubun);
            p.resolve(count);
        });
        return p;
    }

    public Promise getItem(int seq, int gubun) {
        Promise p = new Promise();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            HnB item = dao.getAudioData(seq, gubun);
            p.resolve(item);
        });
        return p;
    }


    //history
    public void setHisotry(int seq, String url, int position, int state) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AudioHistory history = new AudioHistory(seq, url, position, state);
            dao.insertHistory(history);
        });
    }

    public void updateItemHistoryState(HnB item , int state) {
        AppDatabase.databaseWriteExecutor.execute(()->{
            dao.updateItemState(item.getPath(), state);
        });
    }

    public void updateHistoryState(int state) {
        AppDatabase.databaseWriteExecutor.execute(()->{
            dao.updateState(state);
        });
    }


    public Promise getHistroy(String url) {
        Promise p = new Promise();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                AudioHistory history = dao.getHistory(url);
                if (history == null) {
                    history = new AudioHistory();
                }
                p.resolve(history);
            });
        });
        return p;
    }

    public void clearHistory(int seq) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.clearHistory(seq);
        });
    }

    // 리소슨
    public String getAudioPath(HnB data) {
        return application.getFilesDir().getAbsolutePath() + "/" + data.getPath();
    }
}
