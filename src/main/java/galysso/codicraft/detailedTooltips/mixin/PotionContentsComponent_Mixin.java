package galysso.codicraft.detailedTooltips.mixin;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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
                textConsumer.accept(Text.translatable("[SECTION]tooltip.section.effect").formatted(Formatting.WHITE));
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
                    System.out.println(I18n.hasTranslation(statusEffectInstance.getTranslationKey() + ".desc"));
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
/*
        if (!list.isEmpty()) {
            textConsumer.accept(ScreenTexts.EMPTY);
            textConsumer.accept(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));

            for(Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier> pair : list) {
                EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)pair.getSecond();
                double d = entityAttributeModifier.value();
                double e;
                if (entityAttributeModifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE && entityAttributeModifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    e = entityAttributeModifier.value();
                } else {
                    e = entityAttributeModifier.value() * (double)100.0F;
                }

                if (d > (double)0.0F) {
                    textConsumer.accept(Text.translatable("attribute.modifier.plus." + entityAttributeModifier.operation().getId(), new Object[]{AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable(((EntityAttribute)((RegistryEntry)pair.getFirst()).value()).getTranslationKey())}).formatted(Formatting.BLUE));
                } else if (d < (double)0.0F) {
                    e *= (double)-1.0F;
                    textConsumer.accept(Text.translatable("attribute.modifier.take." + entityAttributeModifier.operation().getId(), new Object[]{AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable(((EntityAttribute)((RegistryEntry)pair.getFirst()).value()).getTranslationKey())}).formatted(Formatting.RED));
                }
            }
        }*/
    }
}
