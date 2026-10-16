package com.ooqe.totemindicator.mixin;

import com.ooqe.totemindicator.TotemIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class EntityGlowMixin {
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void totemindicator$forceGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (TotemIndicator.Glowing(self.getUuid())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void totemindicator$glowColor(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;
        if (TotemIndicator.Glowing(self.getUuid())) {
            cir.setReturnValue(TotemIndicator.GlowColor(self.getUuid()));
        }
    }
}