package galysso.codicraft.detailedTooltips.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

// Consumer: Ljava/util/function/Consumer

@Environment(EnvType.CLIENT)
@Mixin(value = ItemStack.class)
public abstract class ItemStack_Mixin {


    @Shadow public abstract ItemEnchantmentsComponent getEnchantments();

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private <T extends TooltipAppender> void onAppendTooltip(
            ComponentType<T> componentType,
            Item.TooltipContext context,
            Consumer<Text> textConsumer,
            TooltipType type,
            CallbackInfo ci
    ) {
        if (componentType.equals(DataComponentTypes.ENCHANTMENTS)) {
            appendEnchantmentTooltip(textConsumer);
            ci.cancel();
        }
    }

    private void appendEnchantmentTooltip(Consumer<Text> textConsumer) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = getEnchantments();
        if (itemEnchantmentsComponent != null) {
            Set<RegistryEntry<Enchantment>> enchantments = itemEnchantmentsComponent.getEnchantments();
            for (RegistryEntry<Enchantment> enchantment : enchantments) {
                String[] idSplitted = enchantment.getIdAsString().toLowerCase().split(":");
                textConsumer.accept(Text.translatable("enchantment." + idSplitted[0] + "." + idSplitted[1] + ".name").formatted(Formatting.DARK_PURPLE));
                if (Screen.hasShiftDown()) {
                    textConsumer.accept(Text.translatable("enchantment." + idSplitted[0] + "." + idSplitted[1] + ".description").formatted(Formatting.GRAY));
                }
            }
        }
    }
}
