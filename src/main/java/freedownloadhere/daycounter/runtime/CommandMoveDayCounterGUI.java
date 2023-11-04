package freedownloadhere.daycounter.runtime;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandMoveDayCounterGUI extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "movedaycountergui";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "Move the Day Counter mod GUI.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if(args.length != 2)
            throw new CommandException("exception.daycounter.movedaycountergui.invalidargnum", new Object[]{args});

        int x, y;

        try
        {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException e)
        {
            throw new CommandException("exception.daycounter.movedaycountergui.invalidargtype", new Object[]{args});
        }

        if (x < 0 || x > Minecraft.getMinecraft().displayWidth || y < 0 || y > Minecraft.getMinecraft().displayHeight)
            throw new CommandException("exception.daycounter.movedaycountergui.illegalargs", new Object[]{args});

        DayCounterGui.theGui.moveGui(x, y);
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                new ChatComponentText("Moved the Day Counter GUI to " + x + " " + y)
        );
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
