package sciwhiz12.basedefense.recipe;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModRecipes;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.item.lock.BrokenPadlockItem;
import sciwhiz12.basedefense.util.UnlockHelper;
import sciwhiz12.basedefense.util.Util;

public class PadlockRepairRecipe extends SpecialRecipe {
    public PadlockRepairRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    private Optional<Triple<Integer, Integer, Integer>> getSlots(CraftingInventory inv) {
        int padlockSlot = -1;
        int keySlot = -1;
        int repairSlot = -1;

        boolean hasInvalid = false;
        for (int slot = 0; slot < inv.getSizeInventory() && !hasInvalid; ++slot) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (item instanceof BrokenPadlockItem) {
                    if (padlockSlot < 0) {
                        padlockSlot = slot;
                    } else {
                        hasInvalid = true;
                    }
                } else if (Tags.Items.INGOTS_IRON.contains(item)) {
                    if (repairSlot < 0) {
                        repairSlot = slot;
                    } else {
                        hasInvalid = true;
                    }
                } else if (stack.getCapability(ModCapabilities.KEY).isPresent()) {
                    if (keySlot < 0) {
                        keySlot = slot;
                    } else {
                        hasInvalid = true;
                    }
                }
            }
        }

        if (!hasInvalid && padlockSlot >= 0 && keySlot >= 0 && repairSlot >= 0) {
            ItemStack lock = inv.getStackInSlot(padlockSlot);
            ItemStack key = inv.getStackInSlot(keySlot);
            if (UnlockHelper.checkUnlock(key, lock, IWorldPosCallable.DUMMY, null)) {
                return Optional.of(Triple.of(padlockSlot, keySlot, repairSlot));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return getSlots(inv).isPresent();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        Optional<Triple<Integer, Integer, Integer>> opSlots = getSlots(inv);
        if (opSlots.isPresent()) {
            ItemStack padlock = inv.getStackInSlot(opSlots.get().getLeft());
            ItemStack output = new ItemStack(ModItems.PADLOCK, 1);
            IColorable.copyColors(padlock, output);
            Util.copyCodes(padlock, output);
            if (padlock.hasDisplayName()) { output.setDisplayName(padlock.getDisplayName()); }
            return output;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        Optional<Triple<Integer, Integer, Integer>> opSlots = getSlots(inv);
        if (opSlots.isPresent()) {
            int keySlot = opSlots.get().getMiddle();
            list.set(keySlot, inv.getStackInSlot(keySlot).copy());
        }

        return list;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.PADLOCK_REPAIR;
    }

}
