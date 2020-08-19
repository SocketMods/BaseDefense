package sciwhiz12.basedefense.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import sciwhiz12.basedefense.Reference.Blocks;
import sciwhiz12.basedefense.Reference.Items;

import static sciwhiz12.basedefense.Reference.MODID;

public class Languages extends LanguageProvider {
    public Languages(DataGenerator gen) {
        super(gen, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlocks();
        addItems();
        addCommands();
        addOthers();
    }

    void addBlocks() {
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
        add(Items.LOCK_CORE, "Lock Core");
        add(Items.PADLOCK, "Padlock");
        add(Items.KEYRING, "Keyring");
        add(Items.BROKEN_LOCK_PIECES, "Broken Lock Pieces");
        add(Items.ADMIN_KEY, "Admin Key");
        add(Items.ADMIN_LOCK_CORE, "Admin Lock Core");
        add(Items.ADMIN_PADLOCK, "Admin Padlock");
    }

    void addCommands() {
        add("command", "admin", "(Admin) ");
        addCommand("chunk", "position", "[%s, %s]");

        addCommandError("chunk.claim", "already_claimed", "Chunk at %s is already claimed");
        addCommandSuccess("chunk.claim", "Chunk at %s has been successfully claimed");

        addCommandError("chunk.unclaim", "not_claimed", "Chunk at %s is not claimed");
        addCommandError("chunk.unclaim", "not_owner", "You do not own the chunk at %s");
        addCommandSuccess("chunk.unclaim", "Chunk at %s has been successfully unclaimed");

        addCommandError("chunk.unclaim_all", "failed", "Nothing changed. There is no claimed chunks to unclaim");
        addCommandSuccess("chunk.unclaim_all", "Successfully unclaimed all chunks for %s");

        addCommand("chunk", "info", "Chunk at %s is owned by %s");
        addCommand("chunk.info", "owner.server", "the server");
        addCommand("chunk.info", "owner.named", "%s");
        addCommand("chunk.info", "owner.unknown", "an unknown entity");
        addCommand("chunk.info", "owner.none", "nobody");
    }

    void addOthers() {
        add("itemGroup." + MODID, "Base Defense");

        add("container", "keysmith", "Keysmithing");
        add("container", "locksmith", "Locksmithing");

        add("tooltip", "admin_only", "for Server Operators!");
        add("tooltip", "color", "Color #%s: %s");
        add("tooltip", "keyring.count", "Contains %s key(s)");
        add("tooltip", "locked_door.has_lock", "Contains installed lock");

        add("tooltip", "codes.header", "Stored Code(s):");
        add("tooltip", "codes.line", "  #%s");
        add("tooltip", "codes.count", "Contains %s codes");
        add("tooltip", "codes.count.zero", "Contains no codes");
        add("tooltip", "codes.count.one", "Contains %s code");

        add("status", "door.locked", "This %s is locked.");
        add("status", "door.info", "\"%s\"");

        add("subtitle", "locked_door.attempt", "Door locked");
        add("subtitle", "locked_door.relock", "Door relocks");
        add("subtitle", "locked_door.unlock", "Door unlocks");
    }

    void addCommandError(final String command, final String identifier, final String value) {
        addCommand(command, "error." + identifier, value);
    }

    void addCommandSuccess(final String command, final String value) {
        addCommand(command, "success", value);
    }

    void addCommand(final String command, final String other, final String value) {
        add("command", command + "." + other, value);
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
