package net.gntc.healing_and_blessing.binding;

import net.gntc.healing_and_blessing.view.SoundView;

import androidx.databinding.BindingAdapter;

public class SoundViewBindingAdapter {

    @BindingAdapter("app:radius")
    public static void Radius(SoundView v,float r){
        v.animateRadius(r);
    }
}
