package sciwhiz12.basedefense.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.client.model.BakedModelWrapper;

public class ISTERWrapper extends BakedModelWrapper<IBakedModel> {
    protected TransformType currentPerspective;

    public ISTERWrapper(IBakedModel parent) {
        this(parent, TransformType.NONE);
    }

    public ISTERWrapper(IBakedModel parent, TransformType perspective) {
        super(parent);
        this.currentPerspective = perspective;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        currentPerspective = cameraTransformType;
        return new ISTERWrapper(originalModel.handlePerspective(cameraTransformType, mat), currentPerspective);
    }

    public TransformType getCurrentPerspective() {
        return currentPerspective;
    }
}
