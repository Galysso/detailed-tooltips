package galysso.codicraft.detailedTooltips.mixin.Minecraft;

import galysso.codicraft.detailedTooltips.Util.DetailedTooltipsUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

// Consumer: Ljava/util/function/Consumer

@Environment(EnvType.CLIENT)
@Mixin(value = ItemStack.class)
public abstract class ItemStack_Mixin implements ComponentHolder {
    @Shadow public abstract ItemEnchantmentsComponent getEnchantments();

    @Shadow public abstract ComponentMap getComponents();

    @Shadow protected abstract <T extends TooltipAppender> void appendTooltip(ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type);

    private static Boolean weaponStatShown;
    private static Boolean weaponModifiersShown;

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void onGetTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (this.get(DataComponentTypes.FOOD) != null) {
            List<Text> tooltips = cir.getReturnValue();
            tooltips.add(1, Text.translatable("codicraft.object_type.food").formatted(Formatting.WHITE));
        }
    }

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private <T extends TooltipAppender> void onAppendTooltip(
            ComponentType<T> componentType,
            Item.TooltipContext context,
            Consumer<Text> textConsumer,
            TooltipType type,
            CallbackInfo ci
    ) {
        if (componentType.equals(DataComponentTypes.ENCHANTMENTS)) {
            appendEnchantmentTooltip_equipment(textConsumer);
            ci.cancel();
        } else if (componentType.equals(DataComponentTypes.STORED_ENCHANTMENTS)) {
            appendEnchantmentTooltip_book(textConsumer);
        }
    }

    @Inject(method = "appendAttributeModifiersTooltip", at = @At("HEAD"))
    private void appendAttributeModifiersTooltip(Consumer<Text> textConsumer, PlayerEntity player, CallbackInfo ci) {
        if (Screen.hasShiftDown()) {
            weaponStatShown = false;
            weaponModifiersShown = false;
        }
    }

    @Inject(method = "appendAttributeModifierTooltip", at = @At("HEAD"))
    private void appendAttributeModifierTooltip(Consumer<Text> textConsumer, PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci) {
        if (Screen.hasShiftDown()) {
            if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID) || modifier.idMatches(Item.BASE_ATTACK_SPEED_MODIFIER_ID)) {
                if (!weaponStatShown) {
                    weaponStatShown = true;
                    textConsumer.accept(Text.literal(DetailedTooltipsUtil.SECTION_SUFFIX).append(Text.translatable("tooltip.section.weapon_stats")).formatted(Formatting.WHITE));
                }
            } else {
                if (!weaponModifiersShown) {
                    double d = modifier.value();
                    if (d < 0.0F || d > 0.0F) {
                        weaponModifiersShown = true;
                        textConsumer.accept(Text.literal(DetailedTooltipsUtil.SECTION_SUFFIX).append(Text.translatable("tooltip.section.attribute_modifiers")).formatted(Formatting.WHITE));
                    }
                }
            }
        }
    }

    @Unique
    private void appendEnchantmentTooltip_equipment(Consumer<Text> textConsumer) {
        appendEnchantmentTooltip(textConsumer, getEnchantments());
    }

    @Unique
    private void appendEnchantmentTooltip_book(Consumer<Text> textConsumer) {
        appendEnchantmentTooltip(textConsumer, this.get(DataComponentTypes.STORED_ENCHANTMENTS));
    }

    @Unique
    private void appendEnchantmentTooltip(Consumer<Text> textConsumer, ItemEnchantmentsComponent itemEnchantmentsComponent) {
        if (itemEnchantmentsComponent != null) {
            Set<RegistryEntry<Enchantment>> enchantments = itemEnchantmentsComponent.getEnchantments();
            if (!enchantments.isEmpty()) {
                if (Screen.hasShiftDown()) {
                    textConsumer.accept((Text.literal(DetailedTooltipsUtil.SECTION_SUFFIX).append(Text.translatable("tooltip.section.enchantment"))).formatted(Formatting.WHITE));
                }
                for (RegistryEntry<Enchantment> enchantment : enchantments) {
                    String[] idSplitted = enchantment.getIdAsString().toLowerCase().split(":");
                    String translationKey = "enchantment." + idSplitted[0] + "." + idSplitted[1];
                    textConsumer.accept(Text.translatable(translationKey).formatted(Formatting.DARK_PURPLE));
                    if (Screen.hasShiftDown()) {
                        if (I18n.hasTranslation(translationKey + ".desc")) {
                            textConsumer.accept(Text.literal("• ").append(Text.translatable(translationKey + ".desc").formatted(Formatting.GRAY)));
                        }
                    }
                }
            }
        }
    }
}
