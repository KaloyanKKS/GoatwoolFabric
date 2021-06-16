package io.github.llamarama.team.goatwoolfabric.mixin;

import io.github.llamarama.team.goatwoolfabric.util.ShearableGoat;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatEntity.class)
public abstract class MixinGoatEntity extends AnimalEntity implements Shearable, ShearableGoat {

    private static final TrackedData<Boolean> SHEARED = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final String SHEARED_KEY = "Sheared";

    public MixinGoatEntity(EntityType<? extends GoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addShearedData(CallbackInfo ci) {
        this.dataTracker.startTracking(SHEARED, false);
    }

    @Override
    public boolean getSheared() {
        return this.dataTracker.get(SHEARED);
    }

    @Override
    public void setSheared(boolean sheared) {
        this.dataTracker.set(SHEARED, sheared);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void interactWithMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!this.world.isClient) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
                itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                this.sheared(SoundCategory.PLAYERS);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
            if (itemStack.isOf(Items.WHEAT) && !this.isShearable()) {
                itemStack.decrement(1);
                this.feed();
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(SHEARED_KEY, this.getSheared());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomData(NbtCompound nbt, CallbackInfo ci) {
        this.setSheared(nbt.getBoolean(SHEARED_KEY));
    }

    public void feed() {
        this.world.playSound(
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_GOAT_EAT, SoundCategory.NEUTRAL, 1.0f, 1.0f, false);
        this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, 0, 0, 0, 0.1, -0.3, 0.1);
        this.setSheared(false);
    }

    @Override
    public void sheared(SoundCategory shearedSoundCategory) {
        this.setSheared(true);
        this.world.playSound(this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_SHEEP_SHEAR,
                shearedSoundCategory, 1.0f, 1.0f, false);

        ItemEntity item = new ItemEntity(this.world,
                this.getX(), this.getY(), this.getZ(),
                new ItemStack(Items.WHITE_WOOL, 1 + this.random.nextInt(2)));

        this.world.spawnEntity(item);
    }

    @Override
    public boolean isShearable() {
        return !this.getSheared() && !this.isBaby();
    }

}
