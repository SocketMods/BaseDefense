package sciwhiz12.basedefense.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import sciwhiz12.basedefense.BaseDefense;

@EventBusSubscriber(modid = BaseDefense.MODID, bus = Bus.MOD)
public class DataGen {
    @SubscribeEvent
    static void onGatherData(GatherDataEvent event) {
        BaseDefense.LOG.debug("Gathering data");
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new Languages(gen));
            gen.addProvider(new BlockStates(gen, event.getExistingFileHelper()));
            gen.addProvider(new ItemModels(gen, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            gen.addProvider(new LootTables(gen));
            gen.addProvider(new Recipes(gen));
        }
    }
}
