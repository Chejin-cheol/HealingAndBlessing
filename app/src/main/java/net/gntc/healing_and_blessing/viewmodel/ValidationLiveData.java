package net.gntc.healing_and_blessing.viewmodel;

import android.util.Log;
import android.widget.Toast;

import net.gntc.healing_and_blessing.view.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class ValidationLiveData<T> extends MutableLiveData<T> {
    private T oldValue = null;
    private List<Observer<T>> container = new ArrayList<Observer<T>>();

    public void observeForever(ValidateObserver<T> observer){
       container.add( new Observer<T>() {
            @Override
            public void onChanged(T t) {
                observer.onChanged(t, oldValue);
                oldValue = t;
            }
        });
       super.observeForever( container.get(container.size() - 1) );
    }

    public void removeAllObserver() {
        for(Observer<T> observer : container){
            super.removeObserver(observer);
        }
    }
}
