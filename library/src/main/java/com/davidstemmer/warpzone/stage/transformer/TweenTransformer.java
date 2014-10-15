package com.davidstemmer.warpzone.stage.transformer;

import android.content.Context;
import android.view.animation.Animation;

import com.davidstemmer.warpzone.SceneCut;
import com.davidstemmer.warpzone.flow.Screenplay;
import com.davidstemmer.warpzone.stage.Scene;

import flow.Flow;

import static android.view.animation.AnimationUtils.loadAnimation;

/**
 * Created by weefbellington on 10/2/14.
 */
public class TweenTransformer implements Scene.Transformer {

    private final Params params;
    private final Context context;

    public static class Params {
        public Params() {}
        public int forwardIn;
        public int forwardOut;
        public int backIn;
        public int backOut;
    }

    public TweenTransformer(Context context, Params params) {
        this.context = context;
        this.params = params;
    }

    @Override
    public void applyAnimations(SceneCut cut, Screenplay screenplay) {

        int out = cut.direction == Flow.Direction.FORWARD ? params.forwardOut : params.backOut;
        int in = cut.direction == Flow.Direction.FORWARD ? params.forwardIn : params.backIn;

        TweenAnimationListener animationListener = new TweenAnimationListener(cut, screenplay);
        if (out != -1 && cut.previousScene != null) {
            Animation anim = loadAnimation(context, out);
            animationListener.addAnimation(anim);
            cut.previousScene.getView().startAnimation(anim);
        }
        if (in != -1 && cut.nextScene != null) {
            Animation anim = loadAnimation(context, in);
            animationListener.addAnimation(anim);
            cut.nextScene.getView().startAnimation(anim);
        }

    }
}
