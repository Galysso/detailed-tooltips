package galysso.codicraft.detailedTooltips.mixin.Iceberg;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.component.IExtendedText;
import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.mojang.datafixers.util.Either;
import galysso.codicraft.detailedTooltips.Util.DetailedTooltipsUtil;
import galysso.codicraft.detailedTooltips.Util.SeparatorTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Language;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(value = Tooltips.class)
public class Tooltips_Mixin {
    @Shadow
    private static boolean tooltipWidthWarningShown;

    @Shadow
    private static Stream<TooltipComponent> splitLine(StringVisitable text, TextRenderer font, int maxWidth) {
        return Stream.empty();
    }

    /**
     * @author Galysso
     * @reason Need to do some computation on the text before it is changed into a TooltipComponent
     */
    @Overwrite
    public static List<TooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends StringVisitable> textElements, Optional<TooltipData> itemComponent, int mouseX, int screenWidth, int screenHeight, TextRenderer forcedFont, TextRenderer fallbackFont, int maxWidth, int index) {
        TextRenderer font = forcedFont == null ? fallbackFont : forcedFont;
        List<Either<StringVisitable, TooltipData>> elements = (List)textElements.stream().map(Either::left).collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent((c) -> elements.add(1, Either.right(c)));
        RenderTooltipEvents.GatherResult eventResult = ((RenderTooltipEvents.Gather)RenderTooltipEvents.GATHER.invoker()).onGather(stack, screenWidth, screenHeight, elements, maxWidth, index);
        if (eventResult.result() != ActionResult.PASS) {
            return List.of();
        } else {
            int tooltipTextWidth = eventResult.tooltipElements().stream().mapToInt((either) -> (Integer)either.map((component) -> {
                try {
                    return font.getWidth(component);
                } catch (Exception e) {
                    if (!tooltipWidthWarningShown) {
                        Iceberg.LOGGER.error("Error rendering tooltip component: \n" + ExceptionUtils.getStackTrace(e));
                        tooltipWidthWarningShown = true;
                    }

                    return 0;
                }
            }, (component) -> 0)).max().orElse(0);
            boolean needsWrap = false;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) {
                    if (mouseX > screenWidth / 2) {
                        tooltipTextWidth = mouseX - 12 - 8;
                    } else {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }

                    needsWrap = true;
                }
            }

            if (eventResult.maxWidth() > 0 && tooltipTextWidth > eventResult.maxWidth()) {
                tooltipTextWidth = eventResult.maxWidth();
                needsWrap = true;
            }

            if (!eventResult.tooltipElements().isEmpty()) {
                Object var17 = ((Either)eventResult.tooltipElements().get(0)).right().orElse((Object)null);
                if (var17 instanceof TooltipComponent) {
                    TooltipComponent clientComponent = (TooltipComponent)var17;
                    if (clientComponent.getWidth(font) < 0) {
                        tooltipTextWidth += clientComponent.getWidth(font);
                    }
                }
            }

            int finalTooltipTextWidth = tooltipTextWidth;

            List<TooltipComponent> res = new ArrayList<>();
            if (needsWrap) {
                for (Either<StringVisitable, TooltipData> either : eventResult.tooltipElements()) {
                    if (either.left().isPresent()) {
                        StringVisitable text = either.left().get();
                        if (text.getString().startsWith(DetailedTooltipsUtil.SECTION_SUFFIX)) {
                            Text newString = Text.literal(text.getString().substring(9));
                            OrderedText orderedText = newString.asOrderedText();
                            TooltipComponent component = TooltipComponent.of(orderedText);
                            if (component instanceof IExtendedText) {
                                ((IExtendedText) component).setAlignment(IExtendedText.TextAlignment.CENTER);
                            }
                            res.add(new SeparatorTooltipComponent());
                            res.add(component);
                        } else {
                            res.addAll(splitLine(text, font, finalTooltipTextWidth).toList());
                        }
                    } else if (either.right().isPresent()) {
                        TooltipData component = either.right().get();
                        res.add(getClientComponent(component));
                    }
                }
            } else {
                for (Either<StringVisitable, TooltipData> either : eventResult.tooltipElements()) {
                    if (either.left().isPresent()) {
                        StringVisitable text = either.left().get();
                        if (text.getString().startsWith(DetailedTooltipsUtil.SECTION_SUFFIX)) {
                            Text newString = Text.literal(text.getString().substring(9));
                            OrderedText orderedText = newString.asOrderedText();
                            TooltipComponent component = TooltipComponent.of(orderedText);
                            if (component instanceof IExtendedText) {
                                ((IExtendedText) component).setAlignment(IExtendedText.TextAlignment.CENTER);
                            }
                            res.add(new SeparatorTooltipComponent());
                            res.add(component);
                        } else {
                            res.add(TooltipComponent.of(text instanceof Text ? ((Text) text).asOrderedText() : Language.getInstance().reorder(text)));
                        }
                    } else if (either.right().isPresent()) {
                        TooltipData component = either.right().get();
                        res.add(getClientComponent(component));
                    }
                }
            }
            return res;
        }
    }

    @Shadow
    private static TooltipComponent getClientComponent(TooltipData componentData) {
        return null;
    }
}