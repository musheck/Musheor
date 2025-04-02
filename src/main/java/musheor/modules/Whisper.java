package musheor.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import musheor.musheck_addon;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;


public class Whisper extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final Setting<Integer> length = sgGeneral.add(new IntSetting.Builder()
        .name("length")
        .description("The length of the generated message")
        .defaultValue(12)
        .min(1)
        .sliderRange(1, 31)
        .build()
    );

    private final Setting<String> PlayerIGN = settings.getDefaultGroup().add(new StringSetting.Builder()
        .name("Player name")
        .description("IGN of the player you want to send a message to.")
        .defaultValue("")
        .build()
    );

    private void onChat() {
        assert mc.player != null;
        String random_message = UUID.randomUUID().toString();
        String short_message = random_message.substring(0, length.get());

        ChatUtils.sendPlayerMsg("/w " + PlayerIGN.get() + " " + short_message);
    }

    public Whisper() {
        super(musheck_addon.CATEGORY, "whisper",
            "Sends a random message to another player upon activating (used for stashmover).");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (this.isActive()) {
            onChat();
            toggle();
        }
    }
}

