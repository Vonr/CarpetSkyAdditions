package com.jsorrell.carpetskyadditions.mixin;

import carpet.CarpetServer;
import com.jsorrell.carpetskyadditions.SkyAdditionsDataComponents;
import com.jsorrell.carpetskyadditions.helpers.SkyAdditionsEnchantmentHelper;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(
            method = "getAvailableEnchantmentResults",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void forceAllowSwiftSneak(
            int modifiedEnchantingLevel,
            ItemStack stack,
            Stream<Holder<Enchantment>> possibleEnchantments,
            CallbackInfoReturnable<List<EnchantmentInstance>> cir,
            List<EnchantmentInstance> list) {
        if (Boolean.TRUE.equals(stack.get(SkyAdditionsDataComponents.SWIFT_SNEAK_ENCHANTABLE_COMPONENT))) {
            var enchantmentRegistry =
                    CarpetServer.minecraft_server.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            if (enchantmentRegistry
                            .getHolderOrThrow(Enchantments.SWIFT_SNEAK)
                            .value()
                            .canEnchant(stack)
                    || stack.is(Items.BOOK)) {
                for (int level = 3; 1 <= level; --level) {
                    if (SkyAdditionsEnchantmentHelper.getSwiftSneakMinCost(level) <= modifiedEnchantingLevel
                            && modifiedEnchantingLevel <= SkyAdditionsEnchantmentHelper.getSwiftSneakMaxCost(level)) {
                        list.add(new EnchantmentInstance(
                                enchantmentRegistry.getHolderOrThrow(Enchantments.SWIFT_SNEAK), level));
                        break;
                    }
                }
            }
        }
    }
}
