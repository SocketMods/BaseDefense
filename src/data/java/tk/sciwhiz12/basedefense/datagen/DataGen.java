package tk.sciwhiz12.basedefense.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import static tk.sciwhiz12.basedefense.BaseDefense.LOG;
import static tk.sciwhiz12.basedefense.Reference.MODID;

@EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public class DataGen {
    public static final Marker DATAGEN = MarkerManager.getMarker("DATAGEN");

    @SubscribeEvent
    static void onGatherData(GatherDataEvent event) {
        LOG.debug(DATAGEN, "Gathering data for data generation");
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            LOG.debug(DATAGEN, "Adding data providers for client assets");
            gen.addProvider(new Languages(gen));
            gen.addProvider(new BlockStates(gen, event.getExistingFileHelper()));
            gen.addProvider(new ItemModels(gen, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            LOG.debug(DATAGEN, "Adding data providers for server data");
            gen.addProvider(new LootTables(gen));
            gen.addProvider(new Recipes(gen));
        }
    }
}
