package galysso.codicraft.detailedTooltips.mixin;

import com.anthonyhilyard.iceberg.component.IExtendedText;
import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
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
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Shadow
    private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {}

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"), cancellable = true)
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci) {
        List<TooltipComponent> list = new ArrayList<>();
        for (Text t : text) {
            if (t.getString().startsWith("[SECTION]")) {
                Text newString = Text.literal(t.getString().substring(9));
                OrderedText orderedText = newString.asOrderedText();
                IExtendedText component = (IExtendedText) TooltipComponent.of(orderedText);
                component.setAlignment(IExtendedText.TextAlignment.CENTER);
                TitleBreakComponent titleBreakComponent = new TitleBreakComponent();
                list.add((TooltipComponent) component);
            } else {
                OrderedText ordered = t.asOrderedText();
                TooltipComponent component = TooltipComponent.of(ordered);
                list.add(component);
            }
        }
        data.ifPresent((datax) -> list.add(list.isEmpty() ? 0 : 1, TooltipComponent.of(datax)));
        drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE);
        ci.cancel();
    }
}
