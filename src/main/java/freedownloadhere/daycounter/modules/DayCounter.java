package freedownloadhere.daycounter.modules;

import freedownloadhere.daycounter.config.State;
import freedownloadhere.daycounter.config.TickCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class DayCounter 
{
    private static DayCounter instance;
    public static void init()
    {
        instance = new DayCounter();
        TickCounter.pause(TickCounter.Type.time);
    }
    public static DayCounter getInstance()
    {
        return instance;
    }

    private DayCounter()
    {
        absolutePath = System.getenv("AppData") + "\\.minecraft\\daycounter.txt";
    }

    @SubscribeEvent
    public void updateTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;

        if(Minecraft.getMinecraft().thePlayer != null && !State.PLAYER_IN_WORLD) onWorldJoin();
        else if(Minecraft.getMinecraft().thePlayer == null && State.PLAYER_IN_WORLD) onWorldExit();

        if(State.IS_PAUSED || !State.PLAYER_IN_WORLD) return;

        calcElapsedTime();
        determineCurrentDay();
        if(TickCounter.isZero(TickCounter.Type.autosave))
        {
            save();
            TickCounter.set(TickCounter.Type.autosave, 200);
        }
    }

    @SubscribeEvent
    public void updateGui(RenderGameOverlayEvent.Text event)
    {
        if(!State.PLAYER_IN_WORLD) return;

        DayCounterGui.displayElapsedTime(lastRecordedDay, secondsElapsed);

        if(TickCounter.get(TickCounter.Type.milestone) > 0)
        {
            DayCounterGui.displayMilestoneTitle(event, lastRecordedDay);
            TickCounter.set(TickCounter.Type.milestone, 80);
        }
    }
    
    public void toggleGui()
    {
        if(!TickCounter.isZero(TickCounter.Type.toggle)) return;

        DayCounterGui.toggleGui();
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                new ChatComponentText( "\u00a77The day counter GUI is now " + (DayCounterGui.isVisible() ? "\u00a7a\u00a7lvisible" : "\u00a7c\u00a7lhidden") + "!")
        );
        TickCounter.set(TickCounter.Type.toggle, 20);
    }
    
    public void resetTime()
    {
        if(!TickCounter.isZero(TickCounter.Type.reset)) return;

        secondsElapsed = 0;
        lastRecordedDay = 0;
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                new ChatComponentText("\u00a77The day counter has been \u00a76\u00a7lreset!")
        );
        TickCounter.set(TickCounter.Type.reset, 20);
    }
    
    public void pauseTime()
    {
        if(!TickCounter.isZero(TickCounter.Type.pause)) return;

        State.IS_PAUSED = !State.IS_PAUSED;

        if(State.IS_PAUSED) TickCounter.pause(TickCounter.Type.time);
        else TickCounter.resume(TickCounter.Type.time);

        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                new ChatComponentText("\u00a77The day counter has been " + (State.IS_PAUSED ? "\u00a73\u00a7lpaused" : "\u00a73\u00a7lunpaused") + "!")
        );

        TickCounter.set(TickCounter.Type.pause, 20);
    }

    private void determineCurrentDay()
    {
        int currentDay = secondsElapsed / DAY_LENGTH_SECONDS;
        if(currentDay > lastRecordedDay)
        {
            Minecraft.getMinecraft().theWorld.playSound(
                    Minecraft.getMinecraft().thePlayer.posX,
                    Minecraft.getMinecraft().thePlayer.posY,
                    Minecraft.getMinecraft().thePlayer.posZ,
                    "random.levelup",
                    100,
                    1,
                    false
            );

            TickCounter.set(TickCounter.Type.milestone, 80);
            lastRecordedDay = currentDay;
        }
    }

    private void calcElapsedTime()
    {
        int secondsToAdd = TickCounter.get(TickCounter.Type.time) / 20;
        if(secondsToAdd > 0)
        {
            TickCounter.set(
                    TickCounter.Type.time,
                    TickCounter.get(TickCounter.Type.time) % 20
            );
            secondsElapsed += secondsToAdd;
        }
    }

    private void onWorldJoin()
    {
        State.PLAYER_IN_WORLD = true;
        load();
    }

    private void onWorldExit()
    {
        State.PLAYER_IN_WORLD = false;
        save();
        TickCounter.set(TickCounter.Type.autosave, 200);
    }

    private void load()
    {
        File file = new File(absolutePath);
        if(!file.exists())
        {
            System.out.println("Could not find file at " + absolutePath);
            return;
        }

        Scanner fileScan;
        try
        {
            fileScan = new Scanner(file);
        }
        catch(Exception e)
        {
            System.out.println(e.getCause().getMessage());
            return;
        }

        secondsElapsed = fileScan.nextInt();
        lastRecordedDay = fileScan.nextInt();
    }

    private void save()
    {
        try
        {
            FileWriter writer = new FileWriter(absolutePath);
            writer.write(secondsElapsed + " " + lastRecordedDay);
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getCause().getMessage());
        }
    }

    private int secondsElapsed = 0;
    private int lastRecordedDay = 0;
    private final String absolutePath;
    private static final int DAY_LENGTH_SECONDS = 1200;
}
