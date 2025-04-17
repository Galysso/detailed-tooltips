package galysso.codicraft.detailedTooltips.mixin.Minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import galysso.codicraft.detailedTooltips.Util.DetailedTooltipsUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.client.resource.language.I18n;

import java.util.List;
import java.util.function.Consumer;

@Mixin(PotionContentsComponent.class)
public class PotionContentsComponent_Mixin {
    private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void buildTooltip(Iterable<StatusEffectInstance> effects, Consumer<Text> textConsumer, float durationMultiplier, float tickRate) {
        textConsumer.accept(Text.translatable("codicraft.object_type.potion").formatted(Formatting.WHITE));
        List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> list = Lists.newArrayList();
        boolean bl = true;

        boolean titlePrinted = false;
        for(StatusEffectInstance statusEffectInstance : effects) {
            if (Screen.hasShiftDown() && !titlePrinted) {
                textConsumer.accept(Text.literal(DetailedTooltipsUtil.SECTION_SUFFIX).append(Text.translatable("tooltip.section.effect")).formatted(Formatting.WHITE));
                titlePrinted = true;
            }
            bl = false;
            MutableText mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
            RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
            ((StatusEffect)registryEntry.value()).forEachAttributeModifier(statusEffectInstance.getAmplifier(), (attribute, modifier) -> list.add(new Pair(attribute, modifier)));
            if (statusEffectInstance.getAmplifier() > 0) {
                mutableText = Text.translatable("potion.withAmplifier", new Object[]{mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier())});
            }

            if (!statusEffectInstance.isDurationBelow(20)) {
                mutableText = Text.translatable("potion.withDuration", new Object[]{mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier, tickRate)});
            }

            if (registryEntry.value().getCategory().equals(StatusEffectCategory.NEUTRAL)) {
                textConsumer.accept(mutableText.formatted(Formatting.DARK_PURPLE));
            } else if (registryEntry.value().getCategory().equals(StatusEffectCategory.HARMFUL)) {
                textConsumer.accept(mutableText.formatted(Formatting.DARK_RED));
            } else if (registryEntry.value().getCategory().equals(StatusEffectCategory.BENEFICIAL)) {
                textConsumer.accept(mutableText.formatted(Formatting.DARK_PURPLE));
            }
            //textConsumer.accept(mutableText.formatted(registryEntry.value().getCategory().getFormatting()));

            if (Screen.hasShiftDown()) {
                if (I18n.hasTranslation(statusEffectInstance.getTranslationKey() + ".desc")) {
                    textConsumer.accept(Text.translatable(statusEffectInstance.getTranslationKey() + ".desc").formatted(Formatting.GRAY));
                }

                ((StatusEffect) registryEntry.value()).forEachAttributeModifier(statusEffectInstance.getAmplifier(), (attribute, modifier) -> {
                    double d = modifier.value();
                    double e;
                    if (modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                        e = modifier.value();
                    } else {
                        e = modifier.value() * (double)100.0F;
                    }

                    if (d > (double)0.0F) {
                        textConsumer.accept(Text.literal("• ").append(Text.translatable("attribute.modifier.plus." + modifier.operation().getId(), new Object[]{AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable((attribute.value()).getTranslationKey())})).formatted(Formatting.GRAY));
                    } else if (d < (double)0.0F) {
                        e *= (double)-1.0F;
                        textConsumer.accept(Text.literal("• ").append(Text.translatable("attribute.modifier.take." + modifier.operation().getId(), new Object[]{AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable((attribute.value()).getTranslationKey())})).formatted(Formatting.GRAY));
                    }
                });
            }
        }

        if (bl) {
            textConsumer.accept(NONE_TEXT);
        }
    }
}
