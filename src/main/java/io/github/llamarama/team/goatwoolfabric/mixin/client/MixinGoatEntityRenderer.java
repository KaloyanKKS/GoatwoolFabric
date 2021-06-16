package io.github.llamarama.team.goatwoolfabric.mixin.client;

import io.github.llamarama.team.goatwoolfabric.util.ShearableGoat;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GoatEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatEntityRenderer.class)
public abstract class MixinGoatEntityRenderer extends MobEntityRenderer<GoatEntity, GoatEntityModel<GoatEntity>> {

    private static final Identifier SHEARED_TEXTURE = new Identifier("textures/entity/goat/sheared_goat.png");
    @Shadow
    @Final
    private static Identifier TEXTURE;


    public MixinGoatEntityRenderer(EntityRendererFactory.Context context, GoatEntityModel<GoatEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/GoatEntity;)Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"), cancellable = true)
    private void addCustomTexture(GoatEntity goatEntity, CallbackInfoReturnable<Identifier> cir) {
        if (goatEntity instanceof ShearableGoat) {
            boolean isSheared = ((ShearableGoat) goatEntity).getSheared();

            cir.setReturnValue(isSheared ? SHEARED_TEXTURE : TEXTURE);
        }
    }

}
