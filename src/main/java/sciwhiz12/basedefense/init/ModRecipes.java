package sciwhiz12.basedefense.init;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.recipe.CopyLockRecipe;

public class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = new DeferredRegister<>(
        ForgeRegistries.RECIPE_SERIALIZERS, BaseDefense.MODID
    );

    public static final RegistryObject<IRecipeSerializer<?>> COPY_LOCK = REGISTER.register(
        "copy_lock", () -> new CopyLockRecipe.Serializer()
    );
}
