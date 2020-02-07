package net.gntc.healing_and_blessing.binding;

import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import net.gntc.healing_and_blessing.R;
import net.gntc.healing_and_blessing.view.SoundView;

import androidx.databinding.BindingAdapter;

public class SoundViewBindingAdapter {

    @BindingAdapter("app:radius")
    public static void Radius(SoundView v,float r){
        v.animateRadius(r);
    }

    @BindingAdapter("app:selected")
    public static void Selected(ImageView v, boolean selected){
        if(selected){
            Animation animation = AnimationUtils.loadAnimation(v.getContext() , R.anim.alpha_anim);
            animation.setDuration(500);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setRepeatCount(Animation.INFINITE);
            v.startAnimation(animation);
        }
        else
        {
            v.setAnimation(null);
        }
    }
}
