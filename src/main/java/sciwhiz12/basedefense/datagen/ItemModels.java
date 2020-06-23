package sciwhiz12.basedefense.datagen;

import static sciwhiz12.basedefense.BaseDefense.MODID;
import static sciwhiz12.basedefense.util.Util.appendPath;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import sciwhiz12.basedefense.init.ModItems;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTextureItem(ModItems.BLANK_KEY);
        singleTextureItem(ModItems.SKELETON_KEY);
        singleTextureItem(ModItems.KEYRING);

        singleTextureItem(ModItems.LOCKED_OAK_DOOR);
        singleTextureItem(ModItems.LOCKED_BIRCH_DOOR);
        singleTextureItem(ModItems.LOCKED_SPRUCE_DOOR);
        singleTextureItem(ModItems.LOCKED_JUNGLE_DOOR);
        singleTextureItem(ModItems.LOCKED_ACACIA_DOOR);
        singleTextureItem(ModItems.LOCKED_DARK_OAK_DOOR);
        singleTextureItem(ModItems.LOCKED_IRON_DOOR);

        coloredItem(ModItems.PADLOCK);
        coloredItem(ModItems.LOCK_CORE);

        coloredKey();
    }

    void coloredKey() {
        final ItemModelBuilder keyParent = getKeyDisplayParent();
        generatedModels.put(keyParent.getLocation(), keyParent);
        final ResourceLocation baseLoc = itemLoc(ModItems.KEY.getRegistryName());
        final ItemModelBuilder base = factory.apply(baseLoc);
        base.parent(keyParent);
        base.texture("layer0", appendPath(baseLoc, "_body"));
        base.texture("layer1", appendPath(baseLoc, "_head"));
        coloredItem(baseLoc, keyParent, base, appendPath(baseLoc, "_body"), appendPath(baseLoc, "_overlay"));
    }

    void singleTextureItem(Item i) {
        final ResourceLocation location = itemLoc(i.getRegistryName());
        ItemModelBuilder builder = factory.apply(location).parent(factory.apply(mcLoc("item/generated")));
        generatedModels.put(location, builder.texture("layer0", location));
    }

    void coloredItem(final Item i) {
        final ModelFile parent = factory.apply(mcLoc("item/generated"));
        final ResourceLocation baseLoc = itemLoc(i.getRegistryName());
        final ItemModelBuilder base = factory.apply(baseLoc);
        base.parent(parent);
        base.texture("layer0", baseLoc);
        coloredItem(itemLoc(i.getRegistryName()), parent, base, baseLoc, baseLoc);
    }

    void coloredItem(final ResourceLocation baseLoc, final ModelFile parent, final ItemModelBuilder defaultModel,
            final ResourceLocation baseTexture, final ResourceLocation overlayTexture) {
        final ResourceLocation color1 = appendPath(baseLoc, "_color_1");
        final ResourceLocation color2 = appendPath(baseLoc, "_color_2");
        final ResourceLocation color3 = appendPath(baseLoc, "_color_3");

        final ItemModelBuilder overlay1 = factory.apply(color1);
        final ItemModelBuilder overlay2 = factory.apply(color2);
        final ItemModelBuilder overlay3 = factory.apply(color3);

        final ItemModelBuilder[] overlays = { overlay1, overlay2, overlay3 };
        for (ItemModelBuilder builder : overlays) {
            builder.parent(parent);
            builder.texture("layer0", baseTexture);
            builder.texture("layer1", overlayTexture);
            builder.texture("layer2", color1);
        }
        overlay2.texture("layer3", color2);
        overlay3.texture("layer3", color2);
        overlay3.texture("layer4", color3);

        defaultModel.override().predicate(new ResourceLocation("colors"), 0).model(defaultModel).end();
        defaultModel.override().predicate(new ResourceLocation("colors"), 1).model(overlay1).end();
        defaultModel.override().predicate(new ResourceLocation("colors"), 2).model(overlay2).end();
        defaultModel.override().predicate(new ResourceLocation("colors"), 3).model(overlay3).end();

        generatedModels.put(baseLoc, defaultModel);
        for (ItemModelBuilder builder : overlays) { generatedModels.put(builder.getLocation(), builder); }
    }

    ItemModelBuilder getKeyDisplayParent() {
        final ItemModelBuilder keyParent = withExistingParent("key_display", mcLoc("item/generated"));
        // @formatter:off
        keyParent.transforms()
                .transform(ModelBuilder.Perspective.GROUND)
                    .rotation(0, 0, 0)
                    .translation(0, 2, 0)
                    .scale(0.5F, 0.5F, 0.5F)
                .end()
                .transform(ModelBuilder.Perspective.HEAD)
                    .rotation(0, 180, 0)
                    .translation(0, 13, 7)
                    .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                    .rotation(180, 0, 0)
                    .translation(0, 3, 1)
                    .scale(0.55F, 0.55F, 0.55F)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                    .rotation(180, -90, 25)
                    .translation(1.13F, 5F, 1.13F)
                    .scale(0.68F, 0.68F, 0.68F)
                .end()
        .end();
        // @formatter:on
        return keyParent;
    }

    ResourceLocation itemLoc(ResourceLocation loc) {
        return new ResourceLocation(loc.getNamespace(), ModelProvider.ITEM_FOLDER + "/" + loc.getPath());
    }
}
