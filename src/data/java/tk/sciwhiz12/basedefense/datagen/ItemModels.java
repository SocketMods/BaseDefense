package tk.sciwhiz12.basedefense.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tk.sciwhiz12.basedefense.Reference.Items;

import static tk.sciwhiz12.basedefense.Reference.MODID;
import static tk.sciwhiz12.basedefense.util.Util.appendPath;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTextureItem(Items.BLANK_KEY);
        singleTextureItem(Items.KEYRING);

        singleTextureItem(Items.LOCKED_OAK_DOOR);
        singleTextureItem(Items.LOCKED_BIRCH_DOOR);
        singleTextureItem(Items.LOCKED_SPRUCE_DOOR);
        singleTextureItem(Items.LOCKED_JUNGLE_DOOR);
        singleTextureItem(Items.LOCKED_ACACIA_DOOR);
        singleTextureItem(Items.LOCKED_DARK_OAK_DOOR);
        singleTextureItem(Items.LOCKED_CRIMSON_DOOR);
        singleTextureItem(Items.LOCKED_WARPED_DOOR);
        singleTextureItem(Items.LOCKED_IRON_DOOR);

        singleTextureItem(Items.ADMIN_LOCK_CORE);
        singleTextureItem(Items.ADMIN_PADLOCK);

        coloredItem(Items.PADLOCK);
        coloredItem(Items.LOCK_CORE);
        coloredItem(Items.BROKEN_LOCK_PIECES);

        final ItemModelBuilder keyParent = getKeyDisplayParent();
        generatedModels.put(keyParent.getLocation(), keyParent);
        coloredKey(keyParent);
        singleTextureItem(Items.ADMIN_KEY, keyParent);
    }

    void coloredKey(ModelFile parent) {
        final ResourceLocation baseLoc = itemLoc(Items.KEY.getRegistryName());
        final ItemModelBuilder base = factory.apply(baseLoc);
        base.parent(parent);
        base.texture("layer0", appendPath(baseLoc, "_body"));
        base.texture("layer1", appendPath(baseLoc, "_head"));
        coloredItem(baseLoc, parent, base, appendPath(baseLoc, "_body"), appendPath(baseLoc, "_overlay"));
    }

    void singleTextureItem(Item i) {
        singleTextureItem(i, factory.apply(mcLoc("item/generated")));
    }

    void singleTextureItem(Item i, ModelFile parent) {
        final ResourceLocation location = itemLoc(i.getRegistryName());
        ItemModelBuilder builder = factory.apply(location).parent(parent);
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
