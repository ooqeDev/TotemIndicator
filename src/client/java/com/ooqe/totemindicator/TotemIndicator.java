package com.ooqe.totemindicator;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TotemIndicator implements ClientModInitializer {
    private static final Map<UUID, Integer> Colors = new ConcurrentHashMap<>();
    private static boolean POV(ClientWorld world, Vec3d from, AbstractClientPlayerEntity target) {return world.raycast(new RaycastContext(from, target.getEyePos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, target)).getType() == HitResult.Type.MISS;}
    public static boolean Glowing(UUID UUID) {return Colors.containsKey(UUID);}
    public static int GlowColor(UUID UUID) {return Colors.get(UUID) == null ? 0xFFFFFF : Colors.get(UUID);}

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(TotemIndicator::GlowTick);
    }

    public static void GlowTick(MinecraftClient client) {
        Colors.clear();
        ClientWorld world = client.world;
        AbstractClientPlayerEntity self = client.player;
        if (world == null || self == null) {return;}

        for (AbstractClientPlayerEntity player : world.getPlayers()) {
            boolean own = player == self;
            if (own && (client.options.getPerspective().isFirstPerson())) {
                continue;
            }
            if (!own && !POV(world, self.getEyePos(), player)) {
                continue;
            }
            if (Check(player)) {
                continue;
            }
            Colors.put(player.getUuid(), ModMenuIntegration.Get().color);
        }
    }

    public static boolean Check(PlayerEntity player) {
        if (ModMenuIntegration.Get().enabled) {
            if (!ModMenuIntegration.Get().armor) {
                if (CheckTool(player)) {
                    return false;
                }
            }
            else if (CheckTool(player) && CheckArmor(player)) {
                return false;
            }
        }
        return true;
    }

    public static boolean CheckTool(PlayerEntity player) {
        if (ModMenuIntegration.Get().shield) {
            if (!player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING) && !player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && !player.getMainHandStack().isOf(Items.SHIELD) && !player.getOffHandStack().isOf(Items.SHIELD)) {
                return true;
            }
        }
        else if (!ModMenuIntegration.Get().shield) {
            if (!player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING) && !player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                return true;
            }
        }
        return false;
    }

    public static boolean CheckArmor(PlayerEntity player) {
        if (ModMenuIntegration.Get().armor) {
            if (player.getInventory().getStack(39).isIn(ItemTags.HEAD_ARMOR) || player.getInventory().getStack(38).isIn(ItemTags.CHEST_ARMOR) || player.getInventory().getStack(37).isIn(ItemTags.LEG_ARMOR) || player.getInventory().getStack(36).isIn(ItemTags.FOOT_ARMOR)) {
                return true;
            }
        }
        return false;
    }
}
