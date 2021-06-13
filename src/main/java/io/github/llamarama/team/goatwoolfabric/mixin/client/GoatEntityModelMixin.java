package io.github.llamarama.team.goatwoolfabric.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.GoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatEntityModel.class)
public abstract class GoatEntityModelMixin<T extends GoatEntity> extends QuadrupedEntityModel<T> {

    protected GoatEntityModelMixin(ModelPart root, boolean headScaled, float childHeadYOffset, float childHeadZOffset, float invertedChildHeadScale, float invertedChildBodyScale, int childBodyYOffset) {
        super(root, headScaled, childHeadYOffset, childHeadZOffset, invertedChildHeadScale, invertedChildBodyScale, childBodyYOffset);
    }

    @Inject(method = "getTexturedModelData", at = @At("HEAD"))
    private static void onGetTexturedModelData(CallbackInfoReturnable<TexturedModelData> cir) {

    }

    @Inject(method = "setAngles", at = @At("HEAD"))
    private void onSetAngles(T goatEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {

    }

}
