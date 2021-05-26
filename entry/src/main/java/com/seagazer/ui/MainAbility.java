package com.seagazer.ui;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;

public class MainAbility extends Ability {

    public static void navigation(Ability starter, Class<? extends Ability> clazz) {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(starter.getBundleName())
                .withAbilityName(clazz)
                .build();
        intent.setOperation(operation);
        starter.startAbility(intent);
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        findComponentById(ResourceTable.Id_animation).setClickedListener(component -> navAnimator());
    }

    private void navAnimator() {
        navigation(this, AnimatorAbility.class);
    }
}
