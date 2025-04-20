package galysso.codicraft.detailedTooltips.mixin.rpginventory;

// Reordering and changing the tooltips that are specific to RPGInventory

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.RPGInventoryClient;
import com.github.theredbrain.rpginventory.config.ClientConfig;
import com.github.theredbrain.rpginventory.registry.ClientEventsRegistry;
import com.github.theredbrain.rpginventory.registry.Tags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ClientEventsRegistry.class, remap = false)
@Environment(EnvType.CLIENT)  // This ensures the Mixin is only applied on the client side
public class RPGInv_ClientEventsRegistry_Mixin {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void initializeClientEvents() {
        ItemTooltipCallback.EVENT.register((ItemTooltipCallback)(stack, context, type, lines) -> {
            ClientConfig clientConfig = RPGInventoryClient.CLIENT_CONFIG;

            ProfileComponent playerCraftedComponent = (ProfileComponent)stack.get(RPGInventory.PLAYER_CRAFTED);
            if (playerCraftedComponent != null && (Boolean)clientConfig.itemTooltipSection.show_item_tooltip_crafted_by_player_name.get()) {
                String formatting_config_string = (String)clientConfig.itemTooltipSection.item_tooltip_crafted_by_player_name_formatting_string.get();
                StringBuilder formatting_string = new StringBuilder();
                if (!formatting_config_string.isEmpty()) {
                    for(int i = 0; i < formatting_config_string.length(); ++i) {
                        formatting_string.append("§").append(formatting_config_string.charAt(i));
                    }
                }

                Object[] var14 = new Object[1];
                String var15 = String.valueOf(formatting_string);
                var14[0] = var15 + playerCraftedComponent.gameProfile().getName();
                lines.add(1, Text.translatable("item.additional_tooltip.player_relation.crafted_by", var14));
            }

            ProfileComponent playerBoundComponent = (ProfileComponent)stack.get(RPGInventory.PLAYER_BOUND);
            if (playerBoundComponent != null && (Boolean)clientConfig.itemTooltipSection.show_item_tooltip_bound_to_player_name.get()) {
                String formatting_config_string = (String)clientConfig.itemTooltipSection.item_tooltip_bound_to_player_name_formatting_string.get();
                StringBuilder formatting_string = new StringBuilder();
                if (!formatting_config_string.isEmpty()) {
                    for(int i = 0; i < formatting_config_string.length(); ++i) {
                        formatting_string.append("§").append(formatting_config_string.charAt(i));
                    }
                }

                Object[] var10002 = new Object[1];
                String var10005 = String.valueOf(formatting_string);
                var10002[0] = var10005 + playerBoundComponent.gameProfile().getName();
                lines.add(1, Text.translatable("item.additional_tooltip.player_relation.bound_to", var10002));
            }

            if (stack.isIn(Tags.TWO_HANDED_ITEMS) && (Boolean)clientConfig.itemTooltipSection.show_item_tooltip_two_handed_items.get()) {
                lines.add(1, Text.translatable("item.additional_tooltip.functionality.two_handed_item"));
            }

            if ((Boolean)clientConfig.itemTooltipSection.show_item_tooltip_equipment_slots.get()) {
                Equipment equipment = Equipment.fromStack(stack);
                if (stack.isIn(Tags.HELMETS) || equipment != null && equipment.getSlotType() == EquipmentSlot.HEAD) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.helmet"));
                }

                if (stack.isIn(Tags.NECKLACES)) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.necklace"));
                }

                if (stack.isIn(Tags.CHEST_PLATES) || equipment != null && equipment.getSlotType() == EquipmentSlot.CHEST) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.chest_plate"));
                }

                if (stack.isIn(Tags.SHOULDERS)) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.shoulders"));
                }

                if (stack.isIn(Tags.GLOVES)) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.gloves"));
                }

                if (stack.isIn(Tags.RINGS)) {
                    if (stack.isIn(Tags.UNIQUE_RINGS)) {
                        lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.ring_unique"));
                    } else {
                        lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.ring"));
                    }
                }

                if (stack.isIn(Tags.BELTS)) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.belt"));
                }

                if (!stack.isIn(Tags.TWO_HANDED_ITEMS)) {
                    if (stack.isIn(Tags.HAND_ITEMS) && stack.isIn(Tags.OFFHAND_ITEMS)) {
                        lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.both_hands"));
                    } else if (stack.isIn(Tags.HAND_ITEMS)) {
                        lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.hand"));
                    } else if (stack.isIn(Tags.OFFHAND_ITEMS)) {
                        lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.offhand"));
                    }
                }

                if (stack.isIn(TagKey.of(RegistryKeys.ITEM, RPGInventory.identifier("spell_book_items")))) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.spell"));
                }
                if (stack.isIn(TagKey.of(RegistryKeys.ITEM, RPGInventory.identifier("relic_items")))) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.charm"));
                }

                if (stack.isIn(Tags.LEGGINGS) || equipment != null && equipment.getSlotType() == EquipmentSlot.LEGS) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.leggings"));
                }

                if (stack.isIn(Tags.BOOTS) || equipment != null && equipment.getSlotType() == EquipmentSlot.FEET) {
                    lines.add(1, Text.translatable("item.additional_tooltip.equipment_slot.boots"));
                }
            }



            // Identified object groups
            if (stack.getItem() instanceof PotionItem) {
                lines.add(1, Text.translatable("codicraft.object_type.potion").formatted(Formatting.WHITE));
            } else if (stack.get(DataComponentTypes.FOOD) != null) {
                lines.add(1, Text.translatable("codicraft.object_type.food").formatted(Formatting.WHITE));
            }

            // Rarity
            if (Screen.hasShiftDown()) {
                lines.add(1, Text.translatable(stack.getRarity().name()).formatted(Formatting.GRAY));
            }

        });
    }
}
