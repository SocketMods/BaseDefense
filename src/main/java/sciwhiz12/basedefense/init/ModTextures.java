package sciwhiz12.basedefense.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.BaseDefense;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModTextures {
    private static final List<ResourceLocation> sprite_list = new ArrayList<>();

    public static final ResourceLocation ATLAS_BLOCKS_TEXTURE = PlayerContainer.LOCATION_BLOCKS_TEXTURE;

    public static final ResourceLocation SLOT_KEY = addSprite("item/slot_key");
    public static final ResourceLocation SLOT_BLANK_KEY = addSprite("item/slot_blank_key");
    public static final ResourceLocation SLOT_LOCK_CORE = addSprite("item/slot_lock_core");
    public static final ResourceLocation SLOT_INGOT_OUTLINE = addSprite("item/slot_ingot_outline");

    private static ResourceLocation addSprite(String location) {
        ResourceLocation loc = new ResourceLocation(BaseDefense.MODID, location);
        sprite_list.add(loc);
        return loc;
    }

    @SubscribeEvent
    public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        BaseDefense.LOG.debug("Adding textures to atlas");
        for (ResourceLocation spriteLoc : sprite_list) { event.addSprite(spriteLoc); }
    }
}
