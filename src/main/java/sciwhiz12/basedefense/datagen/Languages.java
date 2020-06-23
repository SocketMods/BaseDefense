package sciwhiz12.basedefense.datagen;

import static sciwhiz12.basedefense.Reference.MODID;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import sciwhiz12.basedefense.Reference.Blocks;
import sciwhiz12.basedefense.Reference.Items;

public class Languages extends LanguageProvider {
    public Languages(DataGenerator gen) {
        super(gen, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlocks();
        addItems();
        addOthers();
    }

    void addBlocks() {
        add(Blocks.TEST_LOCK_BLOCK, "Lock Testing Block");
        add(Blocks.KEYSMITH_TABLE, "Keysmith Table");
        add(Blocks.LOCKSMITH_TABLE, "Locksmith Table");

        add(Blocks.PADLOCKED_OAK_DOOR, "Padlocked Oak Door");
        add(Blocks.PADLOCKED_BIRCH_DOOR, "Padlocked Birch Door");
        add(Blocks.PADLOCKED_SPRUCE_DOOR, "Padlocked Spruce Door");
        add(Blocks.PADLOCKED_JUNGLE_DOOR, "Padlocked Jungle Door");
        add(Blocks.PADLOCKED_ACACIA_DOOR, "Padlocked Acacia Door");
        add(Blocks.PADLOCKED_DARK_OAK_DOOR, "Padlocked Dark Oak Door");
        add(Blocks.PADLOCKED_IRON_DOOR, "Padlocked Iron Door");

        add(Blocks.LOCKED_OAK_DOOR, "Locked Oak Door");
        add(Blocks.LOCKED_BIRCH_DOOR, "Locked Birch Door");
        add(Blocks.LOCKED_SPRUCE_DOOR, "Locked Spruce Door");
        add(Blocks.LOCKED_JUNGLE_DOOR, "Locked Jungle Door");
        add(Blocks.LOCKED_ACACIA_DOOR, "Locked Acacia Door");
        add(Blocks.LOCKED_DARK_OAK_DOOR, "Locked Dark Oak Door");
        add(Blocks.LOCKED_IRON_DOOR, "Locked Iron Door");
    }

    void addItems() {
        add(Items.BLANK_KEY, "Blank Key");
        add(Items.KEY, "Key");
        add(Items.SKELETON_KEY, "Skeleton Key");
        add(Items.LOCK_CORE, "Lock Core");
        add(Items.PADLOCK, "Padlock");
        add(Items.BROKEN_PADLOCK, "Broken Padlock");
        add(Items.KEYRING, "Keyring");
    }

    void addOthers() {
        add("itemGroup." + MODID, "Base Defense");

        add("container", "keysmith", "Keysmithing");
        add("container", "locksmith", "Locksmithing");

        add("tooltip", "storedcodes", "Stored Codes:");
        add("tooltip", "skeleton_key", "for Server Operators!");
        add("tooltip", "color", "Color #%s: %s");
        add("tooltip", "keyring.count", "Contains %s key(s)");
        add("tooltip", "locked_door.has_lock", "Contains installed lock");

        add("status", "padlocked_door.locked", "This %s is locked.");
        add("status", "padlocked_door.info", "\"%s\"");

        add("subtitle", "locked_door.attempt", "Door locked");
        add("subtitle", "locked_door.relock", "Door relocks");
        add("subtitle", "locked_door.unlock", "Door unlocks");
    }

    /**
     * Convenience method for adding translation keys. Equivalent to
     * {@code add(category + "." + {@link sciwhiz12.basedefense.Reference#MODID} +
     * "." + subKey, value)}.
     */
    void add(final String category, final String subKey, final String value) {
        add(category + "." + MODID + "." + subKey, value);
    }
}
