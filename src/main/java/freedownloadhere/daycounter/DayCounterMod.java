package freedownloadhere.daycounter;

import freedownloadhere.daycounter.proxy.CommonProxy;
import freedownloadhere.daycounter.runtime.DayCounterGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = References.MOD_ID, name = References.MOD_NAME, version = References.VERSION, useMetadata = true)
public class DayCounterMod
{
    @SidedProxy(clientSide = References.CLIENT_PROXY_CLASS, serverSide = References.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        DayCounterGui.theGui = new DayCounterGui();
        MinecraftForge.EVENT_BUS.register(DayCounterGui.theGui);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
        //ClientCommandHandler.instance.registerCommand(new GuiCommands());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}
