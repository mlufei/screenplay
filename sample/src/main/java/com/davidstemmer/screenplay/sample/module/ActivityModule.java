package com.davidstemmer.screenplay.sample.module;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.davidstemmer.screenplay.flow.Screenplay;
import com.davidstemmer.screenplay.sample.MainActivity;
import com.davidstemmer.screenplay.sample.R;
import com.davidstemmer.screenplay.sample.scene.ActionDrawerScene;
import com.davidstemmer.screenplay.sample.scene.DialogScene;
import com.davidstemmer.screenplay.sample.scene.ModalScene;
import com.davidstemmer.screenplay.sample.scene.NavigationDrawerScene;
import com.davidstemmer.screenplay.sample.scene.PagedScene1;
import com.davidstemmer.screenplay.sample.scene.PagedScene2;
import com.davidstemmer.screenplay.sample.scene.SimpleScene;
import com.davidstemmer.screenplay.sample.view.ActionDrawerView;
import com.davidstemmer.screenplay.sample.view.ModalSceneView;
import com.davidstemmer.screenplay.sample.view.NavigationDrawerView;
import com.davidstemmer.screenplay.sample.view.PagedView1;
import com.davidstemmer.screenplay.sample.view.DialogSceneView;
import com.davidstemmer.screenplay.sample.view.WelcomeView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import flow.Backstack;
import flow.Flow;

/**
 * Created by weefbellington on 10/2/14.
 */
@Module(addsTo = ApplicationModule.class,
        injects = {
                ActionDrawerScene.class,
                ActionDrawerScene.Presenter.class,
                ActionDrawerView.class,
                ModalScene.class,
                ModalScene.Presenter.class,
                ModalSceneView.class,
                MainActivity.class,
                NavigationDrawerScene.class,
                NavigationDrawerScene.Presenter.class,
                NavigationDrawerView.class,
                PagedScene1.class,
                PagedScene1.Presenter.class,
                PagedView1.class,
                PagedScene2.class,
                DialogScene.class,
                DialogScene.Presenter.class,
                DialogSceneView.class,
                SimpleScene.class,
                SimpleScene.Presenter.class,
                WelcomeView.class
        })
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides @Singleton
    Activity provideActivity() {
        return activity;
    }

    @Provides @Singleton
    Screenplay provideScreenplay(Activity activity) {
        RelativeLayout container = (RelativeLayout) activity.findViewById(R.id.main);
        return new Screenplay(activity, container);
    }

    @Provides @Singleton
    Flow provideFlow(SimpleScene welcomeStage, Screenplay screenplay) {
        return new Flow(Backstack.single(welcomeStage), screenplay);
    }
}