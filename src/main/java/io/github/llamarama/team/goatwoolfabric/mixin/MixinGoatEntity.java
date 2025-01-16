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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatEntity.class)
public abstract class MixinGoatEntity extends AnimalEntity implements Shearable, ShearableGoat {

    private static final TrackedData<Boolean> SHEARED = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final String SHEARED_KEY = "Sheared";

    protected MixinGoatEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = {"initDataTracker"}, at = {@At("TAIL")})
    private void addShearedData(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(SHEARED, false);
    }

    public boolean getSheared() {
        return this.dataTracker.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.dataTracker.set(SHEARED, sheared);
    }

    @Inject(method = {"interactMob"}, at = {@At("HEAD")}, cancellable = true)
    public void interactWithMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!this.getWorld().isClient) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
                itemStack.damage(1, player);
                this.sheared((ServerWorld) this.getWorld(), SoundCategory.PLAYERS, itemStack);
                cir.setReturnValue(ActionResult.SUCCESS);
            }

            if (this.isBreedingItem(itemStack) && !this.isShearable()) {
                itemStack.decrement(1);
                this.feed();
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = {"writeCustomDataToNbt"}, at = {@At("TAIL")})
    private void writeCustomData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(SHEARED_KEY, this.getSheared());
    }

    @Inject(method = {"readCustomDataFromNbt"}, at = {@At("TAIL")})
    private void readCustomData(NbtCompound nbt, CallbackInfo ci) {
        this.setSheared(nbt.getBoolean(SHEARED_KEY));
    }

    @Unique
    public void feed() {
        this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GOAT_EAT, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
        this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, 0.0, 0.0, 0.0, 0.1, -0.3, 0.1);
        this.setSheared(false);
    }

    @Override
    public void sheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
        this.setSheared(true);
        world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F, false);
        ItemEntity item = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WHITE_WOOL, 1 + this.random.nextInt(2)));
        world.spawnEntity(item);
    }

    @Override
    public boolean isShearable() {
        return !this.getSheared() && !this.isBaby();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.WHEAT);
    }
}
