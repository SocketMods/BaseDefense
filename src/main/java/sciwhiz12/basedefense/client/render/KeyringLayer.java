package sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import sciwhiz12.basedefense.item.key.KeyringItem;

public class KeyringLayer<Player extends PlayerEntity, Model extends BipedModel<Player>>
        extends LayerRenderer<Player, Model> {
    public KeyringLayer(IEntityRenderer<Player, Model> entityRendererIn) {
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
    public static ItemStack getKeyring(PlayerEntity player) {
        PlayerInventory inv = player.inventory;
        if (inv.getCarried().getItem() instanceof KeyringItem) { return inv.getCarried(); }
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
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, Player player,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch) {
        ItemStack stack = getKeyring(player);
        if (!stack.isEmpty()) {
            final FirstPersonRenderer fpRenderer = Minecraft.getInstance().getItemInHandRenderer();
            final Pose pose = player.getPose();
            final boolean crouching = pose == Pose.CROUCHING;
            if (crouching || pose == Pose.STANDING) {
                matrixStack.pushPose();

                final boolean leftHand = player.getMainArm() == HandSide.LEFT;
                matrixStack.translate(-0.08f + (leftHand ? 0.51f : 0f), 0.665f, 0.25f + (crouching ? 0.3f : 0));
                matrixStack.mulPose(Vector3f.YN.rotationDegrees(90f));
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(crouching ? 30f : 45f));
                matrixStack.scale(0.4f, 0.4f, 0.4f);
                // x: left, y: down, z: back

                // TODO: fix the renderer being early of the crouching
                fpRenderer.renderItem(player, stack, ItemCameraTransforms.TransformType.HEAD, leftHand, matrixStack,
                        buffer, light);
                matrixStack.popPose();
            }
        }
    }
}
