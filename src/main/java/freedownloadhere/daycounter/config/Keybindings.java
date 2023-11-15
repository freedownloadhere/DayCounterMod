package freedownloadhere.daycounter.config;

import freedownloadhere.daycounter.modules.DayCounter;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class Keybindings
{
    private static Keybindings instance;
    public static void init()
    {
        instance = new Keybindings();

        keyBindings = new KeyBinding[KEYBINDINGS_COUNT];

        keyBindings[Type.toggleGui.val] = new KeyBinding(
                "key.daycounter.togglegui",
                Keyboard.KEY_NUMPAD0,
                "key.categories.daycounter.gui"
        );

        keyBindings[Type.resetTime.val] = new KeyBinding(
                "key.daycounter.resettime",
                Keyboard.KEY_NUMPAD1,
                "key.categories.daycounter.gui"
        );

        keyBindings[Type.pauseTime.val] = new KeyBinding(
                "key.daycounter.pausetime",
                Keyboard.KEY_NUMPAD2,
                "key.categories.daycounter.gui"
        );

        for(int i = 0; i < KEYBINDINGS_COUNT; i++)
            ClientRegistry.registerKeyBinding(keyBindings[i]);
    }
    public static Keybindings getInstance()
    {
        return instance;
    }

    @SubscribeEvent
    public void handle(InputEvent.KeyInputEvent event)
    {
        if (keyBindings[Type.toggleGui.val].isPressed())
        {
            DayCounter.getInstance().toggleGui();
        }

        if (keyBindings[Type.resetTime.val].isPressed())
        {
            DayCounter.getInstance().resetTime();
        }

        if (keyBindings[Type.pauseTime.val].isPressed())
        {
            DayCounter.getInstance().pauseTime();
        }
    }

    private static KeyBinding[] keyBindings;
    private static final int KEYBINDINGS_COUNT = 3;
    private enum Type
    {
        toggleGui(0),
        resetTime(1),
        pauseTime(2);

        Type(int i){
            val = i;
        }
        int val;
    }
}
