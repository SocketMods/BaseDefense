package sciwhiz12.basedefense.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LockedDoorModel implements IBakedModel {
    private final IBakedModel parentModel;

    public LockedDoorModel(IBakedModel parent) {
        this.parentModel = parent;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return parentModel.getQuads(state, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
        ArrayList<BakedQuad> list = new ArrayList<>();
        for (BakedQuad quad : this.getQuads(state, side, rand)) {
            if (quad.hasTintIndex()) {
                Integer colors = data.getData(LockedDoorTile.COLOR_PROPERTY);
                if (colors != null && colors - 1 >= quad.getTintIndex() - 1) { list.add(quad); }
            } else {
                list.add(quad);
            }
        }
        return list;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return parentModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return parentModel.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return parentModel.isSideLit();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return parentModel.isBuiltInRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return parentModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return parentModel.getOverrides();
    }
}
