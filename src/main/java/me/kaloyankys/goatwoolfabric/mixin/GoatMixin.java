package me.kaloyankys.goatwoolfabric.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatEntity.class)
public abstract class GoatMixin extends AnimalEntity {

    public boolean sheared = false;

    public GoatMixin(EntityType<? extends GoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("TAIL"))
    public void interactWithMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.SHEARS) && !this.isBaby() && !sheared) {
            player.playSound(SoundEvents.ENTITY_GOAT_SCREAMING_DEATH, 1.0F, 1.0F);
            this.world.setBlockState(this.getBlockPos(), (BlockState) Blocks.WHITE_WOOL.getDefaultState());
            this.world.breakBlock(this.getBlockPos(), false);
            this.dropStack(new ItemStack(Blocks.WHITE_WOOL));
            sheared = true;
        }
        if (itemStack.isOf(Items.WHEAT) && !this.isBaby() && sheared) {
            this.world.setBlockState(this.getBlockPos(), (BlockState) Blocks.HAY_BLOCK.getDefaultState());
            this.world.breakBlock(this.getBlockPos(), false);
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, 0, 0,0,0.1,-0.3,0.1);
            sheared = false;
        }
    }
}
