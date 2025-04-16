package galysso.codicraft.detailedTooltips;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetailedTooltipsMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		DetailedTooltipsMain.LOGGER.info("Initialisation du client de Mon Mod!");
	}
}