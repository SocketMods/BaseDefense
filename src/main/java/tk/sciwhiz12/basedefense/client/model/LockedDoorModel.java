package tk.sciwhiz12.basedefense.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.IModelData;
import tk.sciwhiz12.basedefense.tileentity.LockedDoorTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LockedDoorModel implements BakedModel {
    private final BakedModel parentModel;

    public LockedDoorModel(BakedModel parent) {
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
            if (quad.isTinted()) {
                Integer colors = data.getData(LockedDoorTile.COLOR_PROPERTY);
                if (colors != null && colors - 1 >= quad.getTintIndex() - 1) { list.add(quad); }
            } else {
                list.add(quad);
            }
        }
        return list;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return parentModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return parentModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return parentModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return parentModel.isCustomRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return parentModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return parentModel.getOverrides();
    }
}
