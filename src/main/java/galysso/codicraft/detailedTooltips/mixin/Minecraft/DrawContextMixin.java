package galysso.codicraft.detailedTooltips.mixin.Minecraft;

// Trying to make custom tooltips work with EMI: failed
/*
import com.anthonyhilyard.iceberg.component.IExtendedText;
import galysso.codicraft.detailedTooltips.Util.DetailedTooltipsUtil;
import galysso.codicraft.detailedTooltips.Util.SeparatorTooltipComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Shadow
    private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {}

    @Inject(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci) {
        List<TooltipComponent> list = new ArrayList<>();
        for (Text t : text) {
            System.out.println("TEXT: " + t.getString());
            if (t.getString().startsWith(DetailedTooltipsUtil.SECTION_SUFFIX)) {
                Text newString = Text.literal(t.getString().substring(DetailedTooltipsUtil.SECTION_SUFFIX.length()));
                OrderedText orderedText = newString.asOrderedText();
                TooltipComponent component = TooltipComponent.of(orderedText);
                if (component instanceof IExtendedText) {
                    ((IExtendedText) component).setAlignment(IExtendedText.TextAlignment.CENTER);
                }
                list.add(new SeparatorTooltipComponent());
                list.add(component);
            } else {
                list.add(TooltipComponent.of(t.asOrderedText()));
            }
        }
        this.drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE);
        ci.cancel();
        //return list;
    }
}*/