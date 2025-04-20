package galysso.codicraft.detailedTooltips.mixin.EMI;

// Trying to make fancy tooltips work in EMI view


import com.anthonyhilyard.iceberg.component.IExtendedText;
import dev.emi.emi.EmiPort;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.screen.FakeScreen;
import galysso.codicraft.detailedTooltips.Util.DetailedTooltipsUtil;
import galysso.codicraft.detailedTooltips.Util.SeparatorTooltipComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(FakeScreen.class)
public class FakeScreenMixin extends Screen {
    protected FakeScreenMixin(Text title) {
        super(title);
    }

    /**
     * @author Galysso
     * @reason Need to display my custom tooltips
     */
    @Overwrite
    public List<TooltipComponent> getTooltipComponentListFromItem(ItemStack stack) {
        List<TooltipComponent> list = new ArrayList<>();
        List<Text> texts = Screen.getTooltipFromItem(this.client, stack);

        for (Text t : texts) {
            System.out.println("EMI: " + t.getString());
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

        Optional<TooltipData> data = stack.getTooltipData();
        if (data.isPresent()) {
            try {
                list.add(TooltipComponent.of((TooltipData)data.get()));
            } catch (Throwable e) {
                EmiLog.error("Exception converting TooltipComponent", e);
            }
        }

        return list;
    }
}