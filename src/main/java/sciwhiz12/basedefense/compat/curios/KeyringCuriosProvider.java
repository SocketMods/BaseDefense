package sciwhiz12.basedefense.compat.curios;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import sciwhiz12.basedefense.capabilities.KeyringProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Optional;

import static sciwhiz12.basedefense.Reference.Items.KEYRING;

public class KeyringCuriosProvider extends KeyringProvider {
    private final ICurio curio = createCurioCap();
    private final LazyOptional<ICurio> curioCap = LazyOptional.of(() -> curio);

    private ICurio createCurioCap() {
        return new ICurio() {
            @Override
            public boolean canEquip(String identifier, LivingEntity livingEntity) {
                return !CuriosApi.getCuriosHelper().findEquippedCurio(KEYRING, livingEntity).isPresent();
            }

            @Override
            public boolean canRender(String identifier, int index, LivingEntity livingEntity) {
                return true;
            }

            @Override
            public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer,
                    int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks,
                    float ageInTicks, float netHeadYaw, float headPitch) {
                final Optional<ImmutableTriple<String, Integer, ItemStack>> keyring = CuriosApi.getCuriosHelper()
                        .findEquippedCurio(KEYRING, livingEntity);
                keyring.map(ImmutableTriple::getRight).ifPresent(stack -> {
                    final FirstPersonRenderer fpRenderer = Minecraft.getInstance().getFirstPersonRenderer();
                    final Pose pose = livingEntity.getPose();
                    final boolean crouching = pose == Pose.CROUCHING;
                    if (crouching || pose == Pose.STANDING) {
                        matrixStack.push();

                        // TODO: find a way to make this better
                        final boolean leftHand = livingEntity.getPrimaryHand() == HandSide.LEFT;
                        matrixStack.translate(-0.08f + (leftHand ? 0.51f : 0f), 0.665f, 0.25f + (crouching ? 0.3f : 0));
                        // if (leftHand) {
                        //     matrixStack.translate(0.51f, 0f, 0f);
                        // }
                        // if (crouching) {
                        //     matrixStack.translate(0f, 0f, 0.3f);
                        // }
                        matrixStack.rotate(Vector3f.YN.rotationDegrees(90f));
                        matrixStack.rotate(Vector3f.ZP.rotationDegrees(crouching ? 30f : 45f));
                        matrixStack.scale(0.4f, 0.4f, 0.4f);
                        // x: left, y: down, z: back

                        fpRenderer.renderItemSide(livingEntity, stack, TransformType.HEAD, leftHand, matrixStack,
                                renderTypeBuffer, light);

                        matrixStack.pop();
                    }
                });
            }
        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (CuriosCapability.ITEM != null && cap == CuriosCapability.ITEM) {
            return curioCap.cast();
        }
        return super.getCapability(cap, side);
    }
}
