package freedownloadhere.daycounter.mod;

import freedownloadhere.daycounter.config.Reference;
import freedownloadhere.daycounter.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class DayCounterMod
{
    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) { proxy.postInit(event); }
}
