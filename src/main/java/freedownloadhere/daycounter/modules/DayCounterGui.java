package freedownloadhere.daycounter.modules;

import freedownloadhere.daycounter.config.State;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class DayCounterGui
{
    private DayCounterGui() {}

    public static boolean isVisible()
    {
        return showOnScreen;
    }
    public static void toggleGui()
    {
        showOnScreen = !showOnScreen;
    }

    public static void displayElapsedTime(int day, int secondsElapsed)
    {
        if(!showOnScreen) return;

        String currentDay = "Day " + day;

        long secondsUntilNextDay = DAY_LENGTH_SECONDS - (secondsElapsed % DAY_LENGTH_SECONDS);
        String displayedMin = (secondsUntilNextDay / 60 < 10 ? "0" : "") + secondsUntilNextDay / 60;
        String displayedSec = (secondsUntilNextDay % 60 < 10 ? "0" : "") + secondsUntilNextDay % 60;

        String extraInfo = "\u00a7l" + (State.IS_PAUSED ? "\u00a77(Paused) " : "") + displayedMin + ":" + displayedSec;

        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.f);
        GL11.glScalef(1.5f, 1.5f, 1.f);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(currentDay, 0, 0, 0xffffff);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY + 15, 0.f);
        GL11.glColor3f(0xff / 255.f, 0xff / 255.f, 0xff / 255.f);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(extraInfo, 0, 0, 0xffffff);
        GL11.glPopMatrix();
    }

    public static void displayMilestoneTitle(RenderGameOverlayEvent.Text event, int day)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(event.resolution.getScaledWidth(), event.resolution.getScaledHeight() - 30, 0.f);
        GL11.glScalef(3.f, 3.f, 1.f);
        Minecraft.getMinecraft().ingameGUI.drawCenteredString(
                Minecraft.getMinecraft().fontRendererObj,
                "\u00a7lDay \u00a76\u00a7l" + day + " \u00a7f\u00a7lreached!", 0, 0, 0xffffff
        );
        GL11.glPopMatrix();
    }

    public static void moveGui(int x, int y)
    {
        posX = x;
        posY = y;
    }

    private static int posX = 10, posY = 10;
    private static boolean showOnScreen = true;
    private static final int DAY_LENGTH_SECONDS = 1200;
}
