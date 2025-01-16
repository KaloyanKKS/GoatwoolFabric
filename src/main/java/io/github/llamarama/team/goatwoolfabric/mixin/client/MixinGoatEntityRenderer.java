package io.github.llamarama.team.goatwoolfabric.mixin.client;

import io.github.llamarama.team.goatwoolfabric.util.ShearableGoat;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GoatEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.state.GoatEntityRenderState;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.WeakHashMap;

@Mixin(GoatEntityRenderer.class)
public abstract class MixinGoatEntityRenderer extends MobEntityRenderer<GoatEntity, GoatEntityRenderState, GoatEntityModel> {
    @Unique
    private static final Identifier SHEARED_TEXTURE = Identifier.of( "textures/entity/goat/sheared_goat.png");

    @Unique
    private final WeakHashMap<GoatEntityRenderState, GoatEntity> renderStateToEntityMap = new WeakHashMap<>();

    @Shadow
    @Final
    private static Identifier TEXTURE;

    public MixinGoatEntityRenderer(EntityRendererFactory.Context context, GoatEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/passive/GoatEntity;Lnet/minecraft/client/render/entity/state/GoatEntityRenderState;F)V", at = @At("HEAD"))
    public void captureEntityRenderState(GoatEntity goatEntity, GoatEntityRenderState goatEntityRenderState, float f, CallbackInfo ci) {
        renderStateToEntityMap.put(goatEntityRenderState, goatEntity);
    }

    @Inject(method = {"getTexture(Lnet/minecraft/client/render/entity/state/GoatEntityRenderState;)Lnet/minecraft/util/Identifier;"}, at = {@At("HEAD")}, cancellable = true)
    private void addCustomTexture(GoatEntityRenderState goatEntityRenderState, CallbackInfoReturnable<Identifier> cir) {
        GoatEntity goatEntity = renderStateToEntityMap.get(goatEntityRenderState);

        if (goatEntity instanceof ShearableGoat) {
            boolean isSheared = ((ShearableGoat) goatEntity).getSheared();
            cir.setReturnValue(isSheared ? SHEARED_TEXTURE : TEXTURE);
        }
    }

}
