package com.davidstemmer.screenplay.flow;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.davidstemmer.screenplay.SceneCut;
import com.davidstemmer.screenplay.SceneState;
import com.davidstemmer.screenplay.scene.Scene;

import java.util.Iterator;

import flow.Backstack;
import flow.Flow;

/**
 * @version 1.0.0
 * @author  David Stemmer
 * @since   1.0.0
 */
public class Screenplay implements Flow.Listener {

    private final Director director;

    private Scene outgoingScene;
    private SceneState screenState = SceneState.NORMAL;

    public Screenplay(Director director) {
        this.director = director;
    }

    @Override
    public void go(Backstack nextBackstack, Flow.Direction direction, Flow.Callback callback) {

        screenState = SceneState.TRANSITIONING;

        Scene incomingScene = (Scene) nextBackstack.current().getScreen();

        SceneCut sceneCut = new SceneCut.Builder()
                .setDirection(direction)
                .setIncomingScene(incomingScene)
                .setOutgoingScene(outgoingScene)
                .setCallback(callback).build();

        if (incomingScene == outgoingScene) {
            callback.onComplete();
        }

        // Only call incomingScene.setUp if necessary. If we are exiting a modal scene, the View will
        // already exist.
        if (incomingScene.getView() == null) {
            incomingScene.setUp(director.getActivity(), director.getContainer());
        }

        if (direction == Flow.Direction.FORWARD || direction == Flow.Direction.REPLACE) {
            incomingScene.getRigger().layoutIncoming(director.getContainer(), incomingScene.getView(), direction);
            incomingScene.getTransformer().applyAnimations(sceneCut, this);
        }
        else if (outgoingScene != null) {
            outgoingScene.getRigger().layoutIncoming(director.getContainer(), outgoingScene.getView(), direction);
            outgoingScene.getTransformer().applyAnimations(sceneCut, this);
        }
        else {
            // backward, previous scene is null (?)
            callback.onComplete();
        }

        outgoingScene = incomingScene;
    }


    /**
     * Called by the {@link com.davidstemmer.screenplay.scene.Scene.Transformer} after the scene
     * animation completes. Finishes pending layout operations and notifies the Flow.Callback.
     * @param cut contains the next and previous scene, and the flow direction
     */
    public void endCut(SceneCut cut) {
        if (cut.outgoingScene != null) {
            View outgoingView = cut.outgoingScene.tearDown(director.getActivity(), director.getContainer());
            if (cut.direction == Flow.Direction.BACKWARD) {
                cut.outgoingScene.getRigger().layoutOutgoing(director.getContainer(), outgoingView, cut.direction);
            } else {
                cut.incomingScene.getRigger().layoutOutgoing(director.getContainer(), outgoingView, cut.direction);
            }
        }

        cut.callback.onComplete();
        screenState = SceneState.NORMAL;
    }

    /**
     * @return TRANSITIONING if a transition is in process, NORMAL otherwise
     */
    public SceneState getScreenState() {
        return screenState;
    }

    /**
     * Initialize the screen using the current Flow.Backstack. This is expected to be called in
     * Activity.onCreate(). Supports configuration changes.
     * @param flow the current Flow
     */
    public void enter(Flow flow) {
        if (flow.getBackstack().size() == 0) {
            throw new IllegalStateException("Backstack is empty");
        }
        boolean isSceneAttached = false;
        Iterator<Backstack.Entry> iterator = flow.getBackstack().reverseIterator();
        Scene previousScene = null;
        while (iterator.hasNext()) {
            Scene nextScene = (Scene) iterator.next().getScreen();
            if (nextScene.getView() != null) {
                SceneCut cut = new SceneCut.Builder()
                        .setIncomingScene(nextScene)
                        .setOutgoingScene(previousScene)
                        .setDirection(Flow.Direction.FORWARD)
                        .build();
                nextScene.getRigger().layoutIncoming(director.getContainer(), cut.incomingScene.getView(), cut.direction);
                previousScene = nextScene;
                isSceneAttached = true;
            }
        }
        if (!isSceneAttached) {
            flow.replaceTo(flow.getBackstack().current().getScreen());
        }
    }

    /**
     * @version 1.0.0
     * @author  David Stemmer
     * @since   1.0.0
     */
    public interface Director {
        /**
         * @return the current Activity. Should be re-initialized after configuration changes.
         */
        public Activity getActivity();
        /**
         * @return the container for the Flow. Should be re-initialized after configuration changes.
         */
        public ViewGroup getContainer();
    }
}
