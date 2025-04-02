package musheor;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import musheor.modules.AirPlace;
import musheor.modules.HotbarReplenish;
import musheor.modules.Whisper;
import org.slf4j.Logger;

public class musheck_addon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Musheor");
    public static final HudGroup HUD_GROUP = new HudGroup("Modules");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Musheor addon");

        // Modules
        Modules.get().add(new HotbarReplenish());
        Modules.get().add(new AirPlace());
        Modules.get().add(new Whisper());

    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "musheor";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("musheck", "musheck-modules");
    }
}
