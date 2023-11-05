package freedownloadhere.daycounter.runtime;

import freedownloadhere.daycounter.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class DayCounterGui
{
    public static DayCounterGui theGui;

    public DayCounterGui()
    {
        tickCounters = new int[NUMBER_OF_TICKCOUNTERS];
        for(int i = 0; i < 5; i++)
            tickCounters[i] = 0;
        absolutePath = System.getenv("AppData") + "\\.minecraft\\daycounter.txt";
    }

    public void onWorldJoin()
    {
        inWorld = true;
        System.out.println("Joined world!");

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

    public void onWorldExit()
    {
        inWorld = false;
        System.out.println("Left world!");

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
            return;

        if(!isPaused && inWorld) tickCounters[TickCounterTypes.time.toInt]++;
        if(tickCounters[TickCounterTypes.milestone.toInt] > 0) tickCounters[TickCounterTypes.milestone.toInt]--;
        if(tickCounters[TickCounterTypes.toggle.toInt] > 0) tickCounters[TickCounterTypes.toggle.toInt]--;
        if(tickCounters[TickCounterTypes.reset.toInt] > 0) tickCounters[TickCounterTypes.reset.toInt]--;
        if(tickCounters[TickCounterTypes.pause.toInt] > 0) tickCounters[TickCounterTypes.pause.toInt]--;


        if(Minecraft.getMinecraft().thePlayer != null && !inWorld)
            onWorldJoin();
        else if(Minecraft.getMinecraft().thePlayer == null && inWorld)
            onWorldExit();
    }

    @SubscribeEvent
    public void respondToKeypresses(InputEvent.KeyInputEvent event)
    {
        if (ClientProxy.keyBindings[0].isPressed() && tickCounters[TickCounterTypes.toggle.toInt] == 0)
        {
            showOnScreen = !showOnScreen;
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                    new ChatComponentText( "\u00a77The day counter GUI has been " + (showOnScreen ? "\u00a7a\u00a7lenabled" : "\u00a7c\u00a7ldisabled") + "!")
            );
            tickCounters[TickCounterTypes.toggle.toInt] = 20;
        }

        if (ClientProxy.keyBindings[1].isPressed() && tickCounters[TickCounterTypes.reset.toInt] == 0)
        {
            secondsElapsed = 0;
            lastRecordedDay = 0;
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                    new ChatComponentText("\u00a77The day counter has been \u00a76\u00a7lreset!")
            );
            tickCounters[TickCounterTypes.reset.toInt] = 20;
        }

        if (ClientProxy.keyBindings[2].isPressed() && tickCounters[TickCounterTypes.pause.toInt] == 0)
        {
            isPaused = !isPaused;
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                    new ChatComponentText("\u00a77The day counter has been " + (isPaused ? "\u00a73\u00a7lpaused" : "\u00a73\u00a7lunpaused") + "!")
            );
            tickCounters[TickCounterTypes.pause.toInt] = 20;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void update(RenderGameOverlayEvent.Text event)
    {
        if(!inWorld)
            return;

        if(!isPaused)
            calcElapsedTime();

        if(showOnScreen)
            displayElapsedTime();

        if(!isPaused)
            determineCurrentDay();

        if(tickCounters[TickCounterTypes.milestone.toInt] > 0)
            displayMilestoneTitle(
                    event.resolution.getScaledWidth(),
                    event.resolution.getScaledHeight() - 30
            );
    }

    private void calcElapsedTime()
    {
        int secondsToAdd = tickCounters[TickCounterTypes.time.toInt] / 20;
        if(secondsToAdd > 0)
        {
            tickCounters[TickCounterTypes.time.toInt] %= 20;
            secondsElapsed += secondsToAdd;
        }
    }

    private void displayElapsedTime()
    {
        String currentDay = "Day " + this.lastRecordedDay;

        long secondsUntilNextDay = DAY_LENGTH_SECONDS - (secondsElapsed % DAY_LENGTH_SECONDS);
        String displayedMin = (secondsUntilNextDay / 60 < 10 ? "0" : "") + secondsUntilNextDay / 60;
        String displayedSec = (secondsUntilNextDay % 60 < 10 ? "0" : "") + secondsUntilNextDay % 60;

        String extraInfo = "\u00a7l" + (isPaused ? "\u00a77(Paused) " : "") + displayedMin + ":" + displayedSec;

        GL11.glPushMatrix();
        GL11.glScalef(1.5f, 1.5f, 1.f);
        Minecraft.getMinecraft().fontRendererObj.drawString(currentDay, posX, posY, color);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().fontRendererObj.drawString(extraInfo, posX + 5, posY + 20, color);
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

            tickCounters[TickCounterTypes.milestone.toInt] = 80;
            lastRecordedDay = currentDay;
        }
    }

    private void displayMilestoneTitle(int width, int height)
    {
        final float xScale = 3.f, yScale = 3.f;

        GL11.glPushMatrix();
        GL11.glScalef(xScale, yScale, 1.f);
        Minecraft.getMinecraft().ingameGUI.drawCenteredString(
                Minecraft.getMinecraft().fontRendererObj,
                "\u00a7lDay \u00a76\u00a7l" + lastRecordedDay + " \u00a7f\u00a7lreached!",
                width / (int)(2 * xScale),
                height / (int)(2 * yScale),
                color
        );
        GL11.glPopMatrix();
    }

    public void moveGui(int x, int y)
    {
        posX = x;
        posY = y;
    }

    private int posX = 10, posY = 10, color = 0xffffffff;
    private boolean showOnScreen = true;
    private boolean isPaused = false, inWorld = false;
    private int secondsElapsed = 0;
    private int lastRecordedDay = 0;
    private String absolutePath;
    private static final int NUMBER_OF_TICKCOUNTERS = 5;
    private int[] tickCounters;
    private enum TickCounterTypes
    {
        time(0),
        milestone(1),
        toggle(2),
        reset(3),
        pause(4);

        TickCounterTypes(int toInt)
        {
            this.toInt = toInt;
        }

        final int toInt;
    }

    private static final int DAY_LENGTH_SECONDS = 1200;
}
