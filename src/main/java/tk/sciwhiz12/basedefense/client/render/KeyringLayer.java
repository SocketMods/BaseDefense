package tk.sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tk.sciwhiz12.basedefense.item.key.KeyringItem;

public class KeyringLayer<Entity extends Player, Model extends HumanoidModel<Entity>>
    extends RenderLayer<Entity, Model> {
    public KeyringLayer(RenderLayerParent<Entity, Model> entityRendererIn) {
        super(entityRendererIn);
    }

    /**
     * Returns the first {@link ItemStack} with a {@link KeyringItem} in the given player's inventory.
     *
     * <p>The search starts from the player's hotbar, then the rest of the inventory, including the currently selected
     * item. <br/>
     * The player's held item (in both the primary and off hand) are not searched. </p>
     *
     * @param player The player entity
     * @return The {@code ItemStack} with the keyring item, or {@link ItemStack#EMPTY}
     */
    public static ItemStack getKeyring(Player player) {
        Inventory inv = player.getInventory();
        if (inv.getSelected().getItem() instanceof KeyringItem) {
            return inv.getSelected();
        }
        ItemStack primaryHeld = player.getMainHandItem();
        ItemStack offHeld = player.getOffhandItem();
        for (ItemStack stack : inv.items) {
            if (stack.getItem() instanceof KeyringItem && stack != primaryHeld && stack != offHeld) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int light, Entity player,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                       float headPitch) {
        ItemStack stack = getKeyring(player);
        if (!stack.isEmpty()) {
            final ItemInHandRenderer fpRenderer = Minecraft.getInstance().getItemInHandRenderer();
            final Pose pose = player.getPose();
            final boolean crouching = pose == Pose.CROUCHING;
            if (crouching || pose == Pose.STANDING) {
                matrixStack.pushPose();

                final boolean leftHand = player.getMainArm() == HumanoidArm.LEFT;
                matrixStack.translate(-0.08f + (leftHand ? 0.51f : 0f), 0.665f, 0.25f + (crouching ? 0.3f : 0));
                matrixStack.mulPose(Vector3f.YN.rotationDegrees(90f));
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(crouching ? 30f : 45f));
                matrixStack.scale(0.4f, 0.4f, 0.4f);
                // x: left, y: down, z: back

                // TODO: fix the renderer being early of the crouching
                fpRenderer.renderItem(player, stack, ItemTransforms.TransformType.HEAD, leftHand, matrixStack,
                    buffer, light);
                matrixStack.popPose();
            }
        }
    }
}
