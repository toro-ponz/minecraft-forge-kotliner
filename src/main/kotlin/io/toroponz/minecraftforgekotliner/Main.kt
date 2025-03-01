package io.toroponz.minecraftforgekotliner

import com.mojang.logging.LogUtils
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod(Main.MODID)
class Main {
    companion object {
        const val MODID: String = "minecraftforgekotliner"

        private val logger = LogUtils.getLogger()
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onServerStarting(_event: ServerStartingEvent?) {
        logger.info("Hello, world")
    }
}
