package sciwhiz12.basedefense.init;

import static sciwhiz12.basedefense.BaseDefense.COMMON;
import static sciwhiz12.basedefense.BaseDefense.LOG;
import static sciwhiz12.basedefense.util.Util.Null;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.recipe.ColoringRecipe;
import sciwhiz12.basedefense.recipe.CopyCodedLockRecipe;
import sciwhiz12.basedefense.recipe.LockedDoorRecipe;
import sciwhiz12.basedefense.recipe.PadlockRepairRecipe;
import sciwhiz12.basedefense.util.RecipeHelper;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModRecipes {

    public static final RecipeHelper.ShapedSerializer<CopyCodedLockRecipe> COPY_LOCK = Null();
    public static final RecipeHelper.ShapedSerializer<LockedDoorRecipe> LOCKED_DOOR = Null();
    public static final SpecialRecipeSerializer<PadlockRepairRecipe> PADLOCK_REPAIR = Null();
    public static final SpecialRecipeSerializer<ColoringRecipe> COLORING = Null();

    @SubscribeEvent
    static void onRegister(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        LOG.debug(COMMON, "Registering recipe serializers");
        final IForgeRegistry<IRecipeSerializer<?>> reg = event.getRegistry();

        reg.register(new RecipeHelper.ShapedSerializer<>(CopyCodedLockRecipe::new).setRegistryName("copy_lock"));
        reg.register(new RecipeHelper.ShapedSerializer<>(LockedDoorRecipe::new).setRegistryName("locked_door"));
        reg.register(new SpecialRecipeSerializer<>(PadlockRepairRecipe::new).setRegistryName("padlock_repair"));
        reg.register(new SpecialRecipeSerializer<>(ColoringRecipe::new).setRegistryName("coloring"));
    }
}
