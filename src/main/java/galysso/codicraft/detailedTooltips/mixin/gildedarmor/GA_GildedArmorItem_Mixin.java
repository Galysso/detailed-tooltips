package galysso.codicraft.detailedTooltips.mixin.gildedarmor;

// Removing the tooltip specific to Gilded Armors

import com.blocklegend001.gildedarmor.item.GildedArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GildedArmorItem.class)
public class GA_GildedArmorItem_Mixin {
    @Inject(method = "appendTooltip", cancellable = true, at = @At("HEAD"))
    public void appendTooltip(ItemStack itemStack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        ci.cancel();
    }
}
