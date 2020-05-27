package sciwhiz12.basedefense.init;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.recipe.CopyLockRecipe;
import sciwhiz12.basedefense.recipe.PadlockRepairRecipe;

public class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = new DeferredRegister<>(
        ForgeRegistries.RECIPE_SERIALIZERS, BaseDefense.MODID
    );

    public static final RegistryObject<IRecipeSerializer<?>> COPY_LOCK = REGISTER.register(
        "copy_lock", () -> new CopyLockRecipe.Serializer()
    );
    public static final RegistryObject<IRecipeSerializer<?>> PADLOCK_REPAIR = REGISTER.register(
        "padlock_repair", () -> new SpecialRecipeSerializer<>(PadlockRepairRecipe::new)
    );
}
