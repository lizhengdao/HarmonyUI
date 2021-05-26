# HarmonyUI
A ui library for HarmonyOS, include animations, widgets and componentContainers. 

## ValueAnimator
#### 支持`int`和`float`数值设置，支持`component`的平移，缩放，旋转，透明度和布局尺寸的属性动画，提供`restart`和`reverse`的循环模式，`reverse`反向执行动画等功能。
下面列举与原生`AnimatorValue`存在差异化，或者补充功能的`api`，其余`api`的调用方法与鸿蒙保持一致，例如`start()`,`cancel()`,`setDuration`等
```java
        // Example1 构建实例方式1
        // int和float差值动画
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);// ValueAnimator.ofInt(0, 100);
        // component属性动画，支持旋转，缩放，平移，透明度
        ValueAnimator animator = ValueAnimator.ofObject(view, 0, 1f, ValueAnimator.Property.SCALE_X, ValueAnimator.Property.SCALE_Y);

        // Example2 构建实例方式2
        // int和float差值动画
        ValueAnimator animator = new ValueAnimator();
        animator.setFloatValues(0, 1f);// animator3.setIntValues(0, 100);
        // component属性动画，支持旋转，缩放，平移，透明度
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectProperties(view, 0, 1f,
            ValueAnimator.Property.SCALE_X, ValueAnimator.Property.SCALE_Y);
        // 设置循环模式
        animator.setRepeatMode(ValueAnimator.RepeatMode.REVERSE);
        // 设置循环次数
        animator.setRepeatCount(AnimatorValue.INFINITE);
        // 设置差值器
        animator.setInterpolatorType(Animator.CurveType.ACCELERATE_DECELERATE);
        // 添加动画值监听器(不需要使用时记得调用removeUpdateListener)
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(AnimatorValue animatorValue, float fraction, Object animatedValue) {
                float value = (float) animatedValue;
                // fraction： 鸿蒙AnimatorValue的[0,1]
                // animatedValue: 对应android的getAnimatedValue()
            }
        });
        // 以当前为基准，反向执行动画
        animator.reverse();
        // 添加动画执行状态监听器(不需要使用时记得调用removeListener)
        animator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(ValueAnimator animation) {

            }

            @Override
            public void onAnimationEnd(ValueAnimator animation) {

            }

            @Override
            public void onAnimationStop(ValueAnimator animation) {

            }

            @Override
            public void onAnimationCancel(ValueAnimator animation) {

            }

            @Override
            public void onAnimationRepeat(ValueAnimator animation) {

            }

            @Override
            public void onAnimationPause(ValueAnimator animation) {

            }

            @Override
            public void onAnimationResume(ValueAnimator animation) {

            }
        });
```
