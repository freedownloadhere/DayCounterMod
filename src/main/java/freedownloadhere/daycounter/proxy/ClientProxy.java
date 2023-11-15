package freedownloadhere.daycounter.proxy;

import freedownloadhere.daycounter.config.Keybindings;
import freedownloadhere.daycounter.config.TickCounter;
import freedownloadhere.daycounter.modules.DayCounter;
import freedownloadhere.daycounter.commands.CommandMoveDayCounterGUI;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        Keybindings.init();
        TickCounter.init();
        DayCounter.init();
        MinecraftForge.EVENT_BUS.register(Keybindings.getInstance());
        MinecraftForge.EVENT_BUS.register(TickCounter.getInstance());
        MinecraftForge.EVENT_BUS.register(DayCounter.getInstance());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        ClientCommandHandler.instance.registerCommand(new CommandMoveDayCounterGUI());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}
