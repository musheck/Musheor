package musheor.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import musheor.musheck_addon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlaceBlock;

public class AirPlace extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRange = settings.createGroup("Range");
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final Setting<Boolean> render = sgGeneral.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay where the obsidian will be placed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(204, 0, 0, 10))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(204, 0, 0, 255))
        .build()
    );

    // Range

    private final Setting<Boolean> customRange = sgRange.add(new BoolSetting.Builder()
        .name("custom-range")
        .description("Use custom range for air place.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> range = sgRange.add(new DoubleSetting.Builder()
        .name("range")
        .description("Custom range to place at.")
        .visible(customRange::get)
        .defaultValue(5)
        .min(0)
        .sliderMax(6)
        .build()
    );

    private HitResult hitResult;

    public AirPlace() {
        super(musheck_addon.CATEGORY, "air-place",
            "Working airplace for meteorclient.");
    }

    public int tickcounter = 0;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        tickcounter++;
        if (tickcounter % 4 != 0) return;
        assert mc.player != null && mc.getCameraEntity() != null;
        double r = customRange.get() ? range.get() : mc.player.getBlockInteractionRange();
        hitResult = mc.getCameraEntity().raycast(r, 0, false);

        if (!(hitResult instanceof BlockHitResult blockHitResult) || !(mc.player.getMainHandStack().getItem() instanceof BlockItem) && !(mc.player.getMainHandStack().getItem() instanceof SpawnEggItem)) return;

        if (mc.options.useKey.isPressed()) {
            //BlockUtils.place(blockHitResult.getBlockPos(), Hand.MAIN_HAND, mc.player.getInventory().selectedSlot, false, 0, true, true, false);
            airPlace(blockHitResult.getBlockPos(), Direction.DOWN);
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        assert mc.player != null && mc.world != null;
        if (!(hitResult instanceof BlockHitResult blockHitResult)
            || !mc.world.getBlockState(blockHitResult.getBlockPos()).isReplaceable()
            || !(mc.player.getMainHandStack().getItem() instanceof BlockItem) && !(mc.player.getMainHandStack().getItem() instanceof SpawnEggItem)
            || !render.get()) return;

        event.renderer.box(blockHitResult.getBlockPos(), sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    public static boolean airPlace(BlockPos pos, Direction direction) {
        if (mc.player == null || mc.getNetworkHandler() == null || mc.interactionManager == null) return false;

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, direction));

        Hand hand = Hand.OFF_HAND;

        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), direction.getOpposite(), pos, true);

        mc.interactionManager.interactBlock(mc.player, hand, hit);

        mc.player.swingHand(hand, false);

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, direction));

        return true;
    }

    public static boolean airPlace(Item item, BlockPos pos, Direction direction) {
        if (!canPlaceBlock(pos, true, Block.getBlockFromItem(item))) return false;
        switchToItem(item);

        return airPlace(pos, direction);
    }

    public static void switchToItem(Item item) {
        if (mc.player != null) {
            FindItemResult result = getItemSlot(item);
            if (result.found()) {
                InvUtils.swap(result.slot(), false);
            }
        }
    }

    public static FindItemResult getItemSlot(Item item) {
        return InvUtils.findInHotbar(itemStack -> itemStack.getItem() == item);
        }
}
