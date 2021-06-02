package com.seagazer.ui;

import com.seagazer.ui.animation.ValueAnimator;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.animation.Animator;
import ohos.agp.animation.AnimatorValue;
import ohos.agp.components.Text;

public class AnimatorAbility extends Ability {
    private static final int DURATION = 2000;
    private Text target;
    private ValueAnimator number;
    private ValueAnimator scale;
    private ValueAnimator translation;
    private ValueAnimator rotation;
    private ValueAnimator layout;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_animator);

        target = (Text) findComponentById(ResourceTable.Id_target);
        // 数值变化
        findComponentById(ResourceTable.Id_number).setClickedListener(component -> {
            stop();
            number = ValueAnimator.ofInt(0, 100);
            number.setDuration(DURATION);
            number.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animatorValue, float fraction, Object animatedValue) {
                    System.out.println(animatedValue.toString());
                    int v = (int) animatedValue;
                    target.setText(v + "");
                }
            });
            number.start();
        });
        // 缩放
        findComponentById(ResourceTable.Id_scale).setClickedListener(component -> {
            stop();
            target.setText("演示");
            scale = ValueAnimator.ofObject(target, 1f, 1.5f, ValueAnimator.Property.SCALE_X, ValueAnimator.Property.SCALE_Y);
            scale.setDuration(DURATION);
            scale.setInterpolatorType(Animator.CurveType.ACCELERATE);
            scale.setRepeatCount(AnimatorValue.INFINITE);
            scale.setRepeatMode(ValueAnimator.RepeatMode.REVERSE);
            scale.start();
        });
        // 平移
        findComponentById(ResourceTable.Id_translation).setClickedListener(component -> {
            stop();
            translation = ValueAnimator.ofObject(target, 0, 200, ValueAnimator.Property.TRANSLATION_X, ValueAnimator.Property.TRANSLATION_Y);
            translation.setDuration(DURATION);
            translation.setRepeatCount(AnimatorValue.INFINITE);
            translation.setRepeatMode(ValueAnimator.RepeatMode.REVERSE);
            translation.start();
        });
        // 旋转
        findComponentById(ResourceTable.Id_rotation).setClickedListener(component -> {
            stop();
            rotation = ValueAnimator.ofObject(target, 0, 360, ValueAnimator.Property.ROTATION);
            rotation.setDuration(DURATION);
            rotation.setInterpolatorType(Animator.CurveType.ACCELERATE_DECELERATE);
            rotation.setRepeatCount(AnimatorValue.INFINITE);
            rotation.start();

        });
        // 尺寸布局
        findComponentById(ResourceTable.Id_layout).setClickedListener(component -> {
            stop();
            layout = ValueAnimator.ofObject(target, target.getWidth(), target.getWidth() * 2, ValueAnimator.Property.WIDTH);
            layout.setDuration(DURATION);
            layout.setRepeatCount(AnimatorValue.INFINITE);
            layout.setRepeatMode(ValueAnimator.RepeatMode.REVERSE);
            layout.start();
        });
    }

    private void stop() {
        if (number != null) {
            number.cancel();
        }
        if (scale != null) {
            scale.cancel();
        }
        if (translation != null) {
            translation.cancel();
        }
        if (rotation != null) {
            rotation.cancel();
        }
        if (layout != null) {
            layout.cancel();
        }
    }
}
