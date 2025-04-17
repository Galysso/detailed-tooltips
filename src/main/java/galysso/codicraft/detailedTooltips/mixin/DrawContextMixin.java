package galysso.codicraft.detailedTooltips.mixin;

import com.anthonyhilyard.iceberg.component.IExtendedText;
import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Shadow
    private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {}

    public List<Text> capturedTextList;

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"), cancellable = true)
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci) {
        this.capturedTextList = text;
    }

    @ModifyArg(
        method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V"
        ),
        index = 1
    )
    private List<TooltipComponent> modifyTooltipList(List<TooltipComponent> value) {
        List<TooltipComponent> list = new ArrayList<>();
        for (Text t : capturedTextList) {
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
        return list;
    }
}
