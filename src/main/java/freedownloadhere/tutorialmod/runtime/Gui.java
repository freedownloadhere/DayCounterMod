package freedownloadhere.tutorialmod.runtime;

import freedownloadhere.tutorialmod.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.Instant;
import java.util.Scanner;

public class Gui{

    @SubscribeEvent
    public void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(Minecraft.getMinecraft().thePlayer != null)
            return;

        System.out.println("Joined the world!\n");
        lastEpoch = Instant.now().getEpochSecond();
        try
        {
            File file = new File("daycounter.txt");
            Scanner fileScan = new Scanner(file);
            secondsElapsed = fileScan.nextLong();
            lastMilestone = fileScan.nextLong();
        }
        catch (FileNotFoundException e)
        {

        }
    }

    @SubscribeEvent
    public void onWorldExit(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if(event.player.getName() != Minecraft.getMinecraft().thePlayer.getName())
        {
            System.out.println("Player " + event.player.getName() + " is not " + Minecraft.getMinecraft().thePlayer.getName() + "\n");
            return;
        }

        System.out.println("Left the world!\n");
        try
        {
            FileWriter writer = new FileWriter("daycounter.txt");
            writer.write(secondsElapsed + " " + lastMilestone);
            writer.close();
        }
        catch(java.io.IOException e)
        {
            System.out.println("Failed to write to file!\n");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (ClientProxy.keyBindings[0].isPressed())
            {
                showOnScreen = !showOnScreen;
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                        new ChatComponentText( "\u00a77The day counter GUI has been " + (showOnScreen ? "\u00a7a\u00a7lenabled" : "\u00a7c\u00a7ldisabled") + "!")
                );
            }

            if (ClientProxy.keyBindings[1].isPressed())
            {
                /*secondsElapsed = 0;
                lastMilestone = 0;
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                        new ChatComponentText("\u00a77The day counter has been \u00a76\u00a7lreset!")
                );*/
                secondsElapsed += 120;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderGui(RenderGameOverlayEvent.Text event)
    {
        if(Minecraft.getMinecraft().thePlayer == null)
            return;

        calcElapsedTime();

        if(showOnScreen)
            displayElapsedTime();

        if(Math.floor(timeInDays) > lastMilestone)
            displayMilestoneTitle();
    }

    private void calcElapsedTime()
    {
        long timeNow = Instant.now().getEpochSecond();
        if(lastEpoch < timeNow)
        {
            secondsElapsed += (timeNow - lastEpoch);
            lastEpoch = timeNow;
        }

        timeInDays = (secondsElapsed * 1.0 / DAY_LENGTH_SECONDS);
    }

    private void displayElapsedTime()
    {
        double truncatedTimeInDays = Math.floor(timeInDays * 100) / 100;
        String timeElapsed =
                "\u00a7fTime in days: " +
                (truncatedTimeInDays - Math.floor(truncatedTimeInDays) == 0 ? "\u00a7a\u00a7l" : "\u00a7l") +
                truncatedTimeInDays +
                " days";

        Minecraft.getMinecraft().fontRendererObj.drawString(timeElapsed, posX, posY, color);
    }

    private void displayMilestoneTitle()
    {
        lastMilestone = (long)Math.floor(timeInDays);

        Minecraft.getMinecraft().theWorld.playSound(
                Minecraft.getMinecraft().thePlayer.posX,
                Minecraft.getMinecraft().thePlayer.posY,
                Minecraft.getMinecraft().thePlayer.posZ,
                "random.levelup",
                100,
                1,
                false
        );

        Minecraft.getMinecraft().ingameGUI.displayTitle(
                "\u00a7fDay \u00a76\u00a7l" + lastMilestone + " \u00a7freached!",
                "test",
                0,
                10,
                0
        );
    }

    private int posX = 10, posY = 10, color = 0xffffff;
    private boolean showOnScreen = true;
    private long lastEpoch = 0;
    private long secondsElapsed = 0;
    private long lastMilestone = 0;
    private double timeInDays;
    private static final int DAY_LENGTH_SECONDS = 1200;
}
