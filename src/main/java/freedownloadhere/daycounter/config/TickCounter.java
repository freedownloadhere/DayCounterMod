package freedownloadhere.daycounter.config;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickCounter 
{
    private static TickCounter instance;
    public static void init()
    {
        instance = new TickCounter();
    }
    public static TickCounter getInstance() 
    {
        return instance;
    }
    
    private TickCounter()
    {
        tickCounters = new int[NUMBER_OF_TICKCOUNTERS];
        for(int i = 0; i < NUMBER_OF_TICKCOUNTERS; i++) tickCounters[i] = 0;
        increments = new int[] {1, -1, -1, -1, -1, -1};
        paused = new boolean[NUMBER_OF_TICKCOUNTERS];
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void update(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;

        for(int i = 0; i < NUMBER_OF_TICKCOUNTERS; i++)
            if(!paused[i] && tickCounters[i] >= 0) tickCounters[i] += increments[i];
    }
    
    public static void reset(Type tickCounter)
    {
        instance.tickCounters[tickCounter.val] = 0;
    }
    public static void set(Type tickCounter, int value)
    {
        instance.tickCounters[tickCounter.val] = value;
    }
    public static int get(Type tickCounter)
    {
        return instance.tickCounters[tickCounter.val];
    }
    public static boolean isZero(Type tickCounter)
    {
        return instance.tickCounters[tickCounter.val] <= 0;
    }
    public static void pause(Type tickCounter)
    {
        instance.paused[tickCounter.val] = true;
    }
    public static void resume(Type tickCounter)
    {
        instance.paused[tickCounter.val] = false;
    }
    
    private static final int NUMBER_OF_TICKCOUNTERS = 6;
    private final int[] tickCounters, increments;
    private final boolean[] paused;
    public enum Type
    {
        time(0),
        milestone(1),
        toggle(2),
        reset(3),
        pause(4),
        autosave(5);

        Type(int val)
        {
            this.val = val;
        }

        final int val;
    }
}
