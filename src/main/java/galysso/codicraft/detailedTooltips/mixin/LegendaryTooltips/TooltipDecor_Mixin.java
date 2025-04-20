package galysso.codicraft.detailedTooltips.mixin.LegendaryTooltips;

// Draw separators when my custom tooltipComponent is found

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
import com.anthonyhilyard.iceberg.util.Easing;
import com.anthonyhilyard.iceberg.util.GuiHelper;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.tooltip.ItemModelComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import galysso.codicraft.detailedTooltips.Util.SeparatorTooltipComponent;
import io.github.apace100.smwyg.tooltip.HorizontalLayoutTooltipComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(value = TooltipDecor.class)
public class TooltipDecor_Mixin {
    @Shadow
    public static void drawSeparator(MatrixStack poseStack, int x, int y, int width, int color) {}

    @Shadow
    private static float shineTimer;

    @Unique
    private static int capturedOffset;

    @ModifyVariable(
        method = "drawBorder", // or whatever method this block is in
        at = @At("STORE"),
        name = "offset"
    )
    private static int onOffsetUpdate(int value) {
        capturedOffset = value;
        return value;
    }

    @Inject(method = "drawBorder", at = @At("TAIL"))
    private static void drawBorder(MatrixStack poseStack, int x, int y, int width, int height, ItemStack item, List<TooltipComponent> components, TextRenderer font, LegendaryTooltipsConfig.FrameDefinition frameDefinition, boolean comparison, int index, CallbackInfo ci) {
        int additionalOffset = 0;
        for (TooltipComponent component : components) {
            additionalOffset += component.getHeight();
            if (component instanceof ItemModelComponent) {
                additionalOffset -= 13;
            } else if (component instanceof HorizontalLayoutTooltipComponent) {
                additionalOffset -= 10;
            }

            if (component instanceof SeparatorTooltipComponent) {
                if (comparison) {
                    drawSeparator(poseStack, x - 3 + 1, y + capturedOffset + additionalOffset - 23, width, Tooltips.currentColors.borderColorStart());
                } else {
                    drawSeparator(poseStack, x - 3 + 1, y + capturedOffset + additionalOffset - 11, width, Tooltips.currentColors.borderColorStart());
                }
            }
        }
    }
}
