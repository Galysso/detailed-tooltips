package galysso.codicraft.detailedTooltips.Util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public class SeparatorTooltipComponent implements TooltipComponent {
    @Override
    public int getHeight() {
        return 6;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 0;
    }
}
