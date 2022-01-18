package tk.sciwhiz12.basedefense.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

public class ISTERWrapper extends BakedModelWrapper<BakedModel> {
    protected TransformType currentPerspective;

    public ISTERWrapper(BakedModel parent) {
        this(parent, TransformType.NONE);
    }

    public ISTERWrapper(BakedModel parent, TransformType perspective) {
        super(parent);
        this.currentPerspective = perspective;
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
        this.currentPerspective = cameraTransformType;
        originalModel.handlePerspective(cameraTransformType, mat);
        return this;
    }

    public TransformType getCurrentPerspective() {
        return currentPerspective;
    }
}
