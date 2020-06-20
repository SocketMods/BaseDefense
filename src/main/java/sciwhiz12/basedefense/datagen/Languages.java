package sciwhiz12.basedefense.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModItems;

public class Languages extends LanguageProvider {
    public Languages(DataGenerator gen) {
        super(gen, BaseDefense.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlocks();
        addItems();
        addOthers();
    }

    void addBlocks() {
        add(ModBlocks.TEST_LOCK_BLOCK, "Lock Testing Block");
        add(ModBlocks.KEYSMITH_TABLE, "Keysmith Table");
        add(ModBlocks.LOCKSMITH_TABLE, "Locksmith Table");

        add(ModBlocks.PADLOCKED_OAK_DOOR, "Padlocked Oak Door");
        add(ModBlocks.PADLOCKED_BIRCH_DOOR, "Padlocked Birch Door");
        add(ModBlocks.PADLOCKED_SPRUCE_DOOR, "Padlocked Spruce Door");
        add(ModBlocks.PADLOCKED_JUNGLE_DOOR, "Padlocked Jungle Door");
        add(ModBlocks.PADLOCKED_ACACIA_DOOR, "Padlocked Acacia Door");
        add(ModBlocks.PADLOCKED_DARK_OAK_DOOR, "Padlocked Dark Oak Door");
        add(ModBlocks.PADLOCKED_IRON_DOOR, "Padlocked Iron Door");

        add(ModBlocks.LOCKED_OAK_DOOR, "Locked Oak Door");
        add(ModBlocks.LOCKED_BIRCH_DOOR, "Locked Birch Door");
        add(ModBlocks.LOCKED_SPRUCE_DOOR, "Locked Spruce Door");
        add(ModBlocks.LOCKED_JUNGLE_DOOR, "Locked Jungle Door");
        add(ModBlocks.LOCKED_ACACIA_DOOR, "Locked Acacia Door");
        add(ModBlocks.LOCKED_DARK_OAK_DOOR, "Locked Dark Oak Door");
        add(ModBlocks.LOCKED_IRON_DOOR, "Locked Iron Door");
    }

    void addItems() {
        add(ModItems.BLANK_KEY, "Blank Key");
        add(ModItems.KEY, "Key");
        add(ModItems.SKELETON_KEY, "Skeleton Key");
        add(ModItems.LOCK_CORE, "Lock Core");
        add(ModItems.PADLOCK, "Padlock");
        add(ModItems.BROKEN_PADLOCK, "Broken Padlock");
        add(ModItems.KEYRING, "Keyring");
    }

    void addOthers() {
        add("itemGroup." + BaseDefense.MODID, "Base Defense");

        add("container", "keysmith", "Keysmithing");
        add("container", "locksmith", "Locksmithing");

        add("tooltip", "storedcodes", "Stored Codes:");
        add("tooltip", "skeleton_key", "for Server Operators!");
        add("tooltip", "color", "Color #%s: %s");
        add("tooltip", "keyring.count", "Contains %s key(s)");

        add("status", "padlocked_door.locked", "This %s is locked.");
        add("status", "padlocked_door.info", "\"%s\"");

        add("subtitle", "locked_door.attempt", "Door locked");
        add("subtitle", "locked_door.relock", "Door relocks");
        add("subtitle", "locked_door.unlock", "Door unlocks");
    }

    /**
     * Convenience method for adding translation keys. Equivalent to
     * {@code add(category + "." + BaseDefense.MODID + "." + subKey, value)}.
     */
    void add(final String category, final String subKey, final String value) {
        add(category + "." + BaseDefense.MODID + "." + subKey, value);
    }
}
