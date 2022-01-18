package tk.sciwhiz12.basedefense.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import tk.sciwhiz12.basedefense.client.render.PortableSafeItemStackRenderer;

import java.util.function.Consumer;

public class PortableSafeBlockItem extends LockedBlockItem {
    public PortableSafeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return PortableSafeItemStackRenderer.create();
            }
        });
    }
}
