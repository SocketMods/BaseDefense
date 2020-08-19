package sciwhiz12.basedefense;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiConsumer;

import static net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import static net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import static sciwhiz12.basedefense.BaseDefense.COMMON;
import static sciwhiz12.basedefense.BaseDefense.LOG;

public class Config {
    static void registerConfigs(BiConsumer<ModConfig.Type, ForgeConfigSpec> configRegister) {
        LOG.debug(COMMON, "Registering configs");
        configRegister.accept(ModConfig.Type.SERVER, Server.SPEC);
    }

    public static class Server {
        static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        static final ForgeConfigSpec SPEC;

        public static final BooleanValue enableBreakSpeedModifier;

        public static final DoubleValue breakSpeedModifier;

        static {
            enableBreakSpeedModifier = BUILDER.define("enableBreakSpeedModifier", false);

            breakSpeedModifier = BUILDER.defineInRange("breakSpeedModifier", 0.15D, 0D, 1D);

            SPEC = BUILDER.build();
        }
    }
}
