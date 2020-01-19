package net.gntc.healing_and_blessing.room;

import android.content.Context;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HnB.class , AudioHistory.class}, version = 1 , exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DataDao dataDao();
    private static AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE =  Room.databaseBuilder(context,AppDatabase.class,"database")
                            .createFromFile(new File(context.getFilesDir().getAbsolutePath(),"HnB.db"))
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}

