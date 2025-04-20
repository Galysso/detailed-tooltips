package galysso.codicraft.detailedTooltips.mixin.bettercombat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.bettercombat.client.WeaponAttributeTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WeaponAttributeTooltip.class, remap = false)
public class BC_WeaponAttributeTooltip_Mixin {
    @ModifyExpressionValue(
        method = "modifyTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/bettercombat/api/WeaponAttributes;isTwoHanded()Z"
        ),
        remap = false
    )
    private static boolean disableTwoHandedDisplay(boolean original) {
        return false;
    }
}
