package com.seagazer.ui.animation;

import ohos.agp.animation.Animator;
import ohos.agp.animation.AnimatorValue;
import ohos.agp.components.Component;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * ValueAnimator provide full usually function like android.
 */
public class ValueAnimator {
    private AnimatorValue innerAnimator;
    private RepeatMode repeatMode = RepeatMode.RESTART;
    private List<AnimatorUpdateListener> updateListeners;
    private List<AnimatorListener> listeners;
    private final Object[] values = new Object[2];
    private final Object[] reverseValues = new Object[2];
    private boolean takeReverseLogic = false;
    private boolean isReversing = false;
    private WeakReference<Component> targetHolder;
    private Property[] targetProperties;
    private Object currentValue;

    /**
     * Default construct.
     */
    public ValueAnimator() {
        createInnerAnimator();
    }

    /**
     * Default construct.
     *
     * @param target     The target component to be animated.
     * @param properties The properties of component {@link Property}.
     */
    public ValueAnimator(Component target, Property... properties) {
        createInnerAnimator();
        targetHolder = new WeakReference<>(target);
        targetProperties = properties;
    }

    /**
     * The properties of component can be animated.
     */
    public enum Property {
        SCALE_X, SCALE_Y, TRANSLATION_X, TRANSLATION_Y, ALPHA, ROTATION, WIDTH, HEIGHT
    }

    /**
     * Create a ValueAnimator instance by initial float values.
     *
     * @param target     The target component to be animated.
     * @param start      The start value.
     * @param end        The end value.
     * @param properties The properties of component {@link Property}.
     * @return ValueAnimator instance.
     */
    public static ValueAnimator ofObject(Component target, float start, float end, Property... properties) {
        ValueAnimator valueAnimator = new ValueAnimator(target, properties);
        valueAnimator.setFloatValues(start, end);
        return valueAnimator;
    }

    /**
     * Create a ValueAnimator instance by initial float values.
     *
     * @param start The start value.
     * @param end   The end value.
     * @return ValueAnimator instance.
     */
    public static ValueAnimator ofFloat(float start, float end) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(start, end);
        return valueAnimator;
    }

    /**
     * Create a ValueAnimator instance by initial int values.
     *
     * @param start The start value.
     * @param end   The end value.
     * @return ValueAnimator instance.
     */
    public static ValueAnimator ofInt(int start, int end) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(start, end);
        return valueAnimator;
    }

    /**
     * Setup component and properties to bind valueAnimator.
     *
     * @param target     The target component to be animated.
     * @param start      The start value.
     * @param end        The end value.
     * @param properties The properties of component {@link Property}.
     */
    public void setObjectProperties(Component target, float start, float end, Property... properties) {
        targetHolder = new WeakReference<>(target);
        targetProperties = properties;
        setFloatValues(start, end);
    }

    /**
     * Set the start and end int value for valueAnimator.
     *
     * @param start The start value.
     * @param end   The end value.
     */
    public void setIntValues(int start, int end) {
        values[0] = start;
        values[1] = end;
        reverseValues[0] = end;
        reverseValues[1] = start;
    }

    /**
     * Set the start and end float value for valueAnimator.
     *
     * @param start The start value.
     * @param end   The end value.
     */
    public void setFloatValues(float start, float end) {
        values[0] = start;
        values[1] = end;
        reverseValues[0] = end;
        reverseValues[1] = start;
    }

    /**
     * Read the current animated value, maybe null.
     *
     * @return The current animated value.
     */
    public Object getAnimatedValue() {
        return currentValue;
    }

    /**
     * Get the real animator.
     *
     * @return The real animator.
     */
    public AnimatorValue getInnerAnimator() {
        return innerAnimator;
    }

    private void createInnerAnimator() {
        innerAnimator = new AnimatorValue();
        innerAnimator.setValueUpdateListener(valueUpdateListener);
        innerAnimator.setLoopedListener(loopedListener);
    }

    private final AnimatorValue.ValueUpdateListener valueUpdateListener = new AnimatorValue.ValueUpdateListener() {
        @Override
        public void onUpdate(AnimatorValue animator, float fraction) {
            Object[] takeValues = values;
            if (takeReverseLogic && isReversing) {
                takeValues = reverseValues;
            }
            Object animatedValue = takeValues[0];
            if (animatedValue != null) {
                if (animatedValue instanceof Integer) {
                    int start = (int) takeValues[0];
                    int end = (int) takeValues[1];
                    animatedValue = start + (int) (fraction * (end - start));
                } else {
                    float start = (float) takeValues[0];
                    float end = (float) takeValues[1];
                    animatedValue = start + fraction * (end - start);
                }
            }
            currentValue = animatedValue;
            if (updateListeners != null) {
                notifyOuterListener(ValueAnimator.this, fraction, animatedValue);
            }
            if (targetHolder != null && targetHolder.get() != null) {
                updateComponentProperty((Float) animatedValue);
            }
        }
    };

    private void notifyOuterListener(ValueAnimator animator, float fraction, Object currentValue) {
        for (AnimatorUpdateListener listener : updateListeners) {
            listener.onAnimationUpdate(animator, fraction, currentValue);
        }
    }

    private void updateComponentProperty(Float currentValue) {
        Component component = targetHolder.get();
        for (Property property : targetProperties) {
            switch (property) {
                case SCALE_X:
                    component.setScaleX(currentValue);
                    break;
                case SCALE_Y:
                    component.setScaleY(currentValue);
                    break;
                case TRANSLATION_X:
                    component.setTranslationX(currentValue);
                    break;
                case TRANSLATION_Y:
                    component.setTranslationY(currentValue);
                    break;
                case ALPHA:
                    component.setAlpha(currentValue);
                    break;
                case ROTATION:
                    component.setRotation(currentValue);
                    break;
                case WIDTH:
                    float w = currentValue;
                    component.setWidth((int) w);
                    break;
                case HEIGHT:
                    float h = currentValue;
                    component.setHeight((int) h);
                    break;
            }
        }
    }

    private final Animator.LoopedListener loopedListener = new Animator.LoopedListener() {
        @Override
        public void onRepeat(Animator animator) {
            if (takeReverseLogic) {
                isReversing = !isReversing;
            }
            if (listeners != null) {
                for (AnimatorListener listener : listeners) {
                    listener.onAnimationRepeat(ValueAnimator.this);
                }
            }
        }
    };

    /**
     * Mode for repeat play.
     */
    public enum RepeatMode {
        RESTART, REVERSE
    }

    /**
     * Set the repeat mode when the repeat count is INFINITE.
     *
     * @param repeatMode The mode for repeat play.{@link RepeatMode}
     */
    public void setRepeatMode(RepeatMode repeatMode) {
        if (innerAnimator.isRunning()) {
            throw new RuntimeException("You can not set repeat mode when the animation is running!");
        }
        this.repeatMode = repeatMode;
    }

    /**
     * Sets the duration of the animation.
     *
     * @param duration The length of the animation, in milliseconds.
     */
    public void setDuration(long duration) {
        innerAnimator.setDuration(duration);
    }

    /**
     * Sets how many times the animation should be repeated. If the repeat
     * count is 0, the animation is never repeated. If the repeat count is
     * greater than 0 or {@link Animator#INFINITE}, the repeat mode will be taken
     * into account. The repeat count is 0 by default.
     *
     * @param value the number of times the animation should be repeated
     */
    public void setRepeatCount(int value) {
        innerAnimator.setLoopedCount(value);
    }

    /**
     * The amount of time, in milliseconds, to delay starting the animation after
     * {@link #start()} is called. Note that the start delay should always be non-negative. Any
     * negative start delay will be clamped to 0 on N and above.
     *
     * @param startDelay The amount of the delay, in milliseconds
     */
    public void setStartDelay(long startDelay) {
        innerAnimator.setDelay(startDelay);
    }


    /**
     * The time interpolator used in calculating the elapsed fraction of this animation. The
     * interpolator determines whether the animation runs with linear or non-linear motion,
     * such as acceleration and deceleration. The default value is
     *
     * @param value the interpolator to be used by this animation. {@link Animator.CurveType}
     */
    public void setInterpolatorType(int value) {
        innerAnimator.setCurveType(value);
    }


    /**
     * The time interpolator used in calculating the elapsed fraction of this animation. The
     * interpolator determines whether the animation runs with linear or non-linear motion,
     * such as acceleration and deceleration. The default value is
     *
     * @param value the interpolator to be used by this animation.
     */
    public void setInterpolator(Animator.TimelineCurve value) {
        innerAnimator.setCurve(value);
    }

    /**
     * Adds a listener to the set of listeners that are sent events through the life of an animation,
     * such as start, repeat, and end.
     *
     * @param listener the listener to be added to the current set of listeners for this animation.
     */
    public void addListener(AnimatorListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
            innerAnimator.setStateChangedListener(new Animator.StateChangedListener() {
                @Override
                public void onStart(Animator animator) {
                    for (AnimatorListener listener : listeners) {
                        listener.onAnimationStart(ValueAnimator.this);
                    }
                }

                @Override
                public void onStop(Animator animator) {
                    for (AnimatorListener listener : listeners) {
                        listener.onAnimationStop(ValueAnimator.this);
                    }
                }

                @Override
                public void onCancel(Animator animator) {
                    for (AnimatorListener listener : listeners) {
                        listener.onAnimationCancel(ValueAnimator.this);
                    }
                }

                @Override
                public void onEnd(Animator animator) {
                    for (AnimatorListener listener : listeners) {
                        listener.onAnimationEnd(ValueAnimator.this);
                    }
                }

                @Override
                public void onPause(Animator animator) {
                    for (AnimatorListener listener : listeners) {
                        listener.onAnimationPause(ValueAnimator.this);
                    }
                }

                @Override
                public void onResume(Animator animator) {
                    for (AnimatorListener listener : listeners) {
                        listener.onAnimationResume(ValueAnimator.this);
                    }
                }
            });
        }
        listeners.add(listener);
    }

    /**
     * Removes a listener from the set listening to this animation.
     *
     * @param listener the listener to be removed from the current set of listeners for this animation.
     */
    public void removeListener(AnimatorListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
        if (listeners.size() == 0) {
            listeners = null;
        }
    }

    /**
     * Adds a listener to the set of listeners that are sent update events through the life of
     * an animation. This method is called on all listeners for every frame of the animation,
     * after the values for the animation have been calculated.
     *
     * @param listener the listener to be added to the current set of listeners for this animation.
     */
    public void addUpdateListener(AnimatorUpdateListener listener) {
        if (updateListeners == null) {
            updateListeners = new ArrayList<>();
        }
        updateListeners.add(listener);
    }

    /**
     * Removes a listener from the set listening to frame updates for this animation.
     *
     * @param listener the listener to be removed from the current set of update listeners
     *                 for this animation.
     */
    public void removeUpdateListener(AnimatorUpdateListener listener) {
        if (updateListeners == null) {
            return;
        }
        updateListeners.remove(listener);
        if (updateListeners.size() == 0) {
            updateListeners = null;
        }
    }

    /**
     * Removes all listeners from the set listening to frame updates for this animation.
     */
    public void removeAllUpdateListeners() {
        if (updateListeners == null) {
            return;
        }
        updateListeners.clear();
        updateListeners = null;
    }

    /**
     * Start the animation playing.
     */
    public void start() {
        if (innerAnimator.getLoopedCount() == AnimatorValue.INFINITE) {
            // looper mode
            if (repeatMode == RepeatMode.REVERSE) {
                // if reverse mode, take custom logic: reverse animation value when update
                takeReverseLogic = true;
            }
        }
        innerAnimator.start();
    }

    /**
     * Plays the ValueAnimator in reverse.
     */
    public void reverse() {
        takeReverseLogic = !takeReverseLogic;
        isReversing = !isReversing;
        if (innerAnimator.isRunning()) {
            innerAnimator.end();
        }
        innerAnimator.start();
    }

    /**
     * Stop the animation.
     */
    public void stop() {
        innerAnimator.stop();
    }

    /**
     * Cancel the animation.Unlike {@link #end()}, this causes the animation to stop in its tracks.
     */
    public void cancel() {
        innerAnimator.cancel();
    }

    /**
     * End the animation.
     */
    public void end() {
        innerAnimator.end();
    }

    /**
     * Pauses a running animation.
     */
    public void pause() {
        innerAnimator.pause();
    }

    /**
     * Resumes a paused animation.
     */
    public void resume() {
        innerAnimator.resume();
    }

    /**
     * Returns whether this animator is currently in a paused state.
     *
     * @return True if the animator is currently paused, false otherwise.
     */
    public boolean isPaused() {
        return innerAnimator.isPaused();
    }

    /**
     * Returns whether this Animator is currently running (having been started and gone past any
     * initial startDelay period and not yet ended).
     *
     * @return Whether the Animator is running.
     */
    public boolean isRunning() {
        return innerAnimator.isRunning();
    }

    /**
     * Gets the duration of the animation.
     *
     * @return The length of the animation, in milliseconds.
     */
    public long getDuration() {
        return innerAnimator.getDuration();
    }

    /**
     * The amount of time, in milliseconds, to delay processing the animation
     * after {@link #start()} is called.
     *
     * @return the number of milliseconds to delay running the animation
     */
    public long getStartDelay() {
        return innerAnimator.getDelay();
    }

    /**
     * Defines how many times the animation should repeat. The default value
     * is 0.
     *
     * @return the number of times the animation should repeat, or {@link Animator#INFINITE}
     */
    public int getRepeatCount() {
        return innerAnimator.getLoopedCount();
    }

    /**
     * Returns the timing interpolator that this ValueAnimator uses.
     *
     * @return The timing interpolator for this ValueAnimator.
     */
    public int getInterpolatorType() {
        return innerAnimator.getCurveType();
    }

    /**
     * Implementors of this interface can add themselves as update listeners
     * to an ValueAnimator instance to receive callbacks on every animation
     * frame, after the current frame's values have been calculated for that
     * ValueAnimator.
     */
    public interface AnimatorUpdateListener {
        /**
         * Notifies the occurrence of another frame of the animation.
         *
         * @param animator      The animation current playing.
         * @param fraction      The fraction of animation from 0 to 1.
         * @param animatedValue The animate value of current frame.
         */
        void onAnimationUpdate(ValueAnimator animator, float fraction, Object animatedValue);
    }

    /**
     * Animator state changed listener.
     */
    public interface AnimatorListener {
        /**
         * <p>Notifies the start of the animation.</p>
         *
         * @param animation The started animation.
         */
        void onAnimationStart(ValueAnimator animation);

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animation The animation which reached its end.
         */
        void onAnimationEnd(ValueAnimator animation);

        /**
         * <p>Notifies that the animation was stop.
         *
         * @param animation The animation being resumed.
         */
        void onAnimationStop(ValueAnimator animation);

        /**
         * <p>Notifies the cancellation of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animation The animation which was canceled.
         */
        void onAnimationCancel(ValueAnimator animation);

        /**
         * <p>Notifies the repetition of the animation.</p>
         *
         * @param animation The animation which was repeated.
         */
        void onAnimationRepeat(ValueAnimator animation);

        /**
         * <p>Notifies that the animation was paused.</p>
         *
         * @param animation The animation being paused.
         */
        void onAnimationPause(ValueAnimator animation);

        /**
         * <p>Notifies that the animation was resumed, after being
         * previously paused.</p>
         *
         * @param animation The animation being resumed.
         * @see #resume()
         */
        void onAnimationResume(ValueAnimator animation);
    }
}
