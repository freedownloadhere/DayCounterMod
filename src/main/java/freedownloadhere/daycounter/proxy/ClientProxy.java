package freedownloadhere.daycounter.proxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy
{
    public static KeyBinding[] keyBindings;
    public static final int KEYBINDINGS_COUNT = 3;

    @Override
    public void init()
    {
        keyBindings = new KeyBinding[KEYBINDINGS_COUNT];

        keyBindings[0] = new KeyBinding(
                "key.daycounter.togglegui",
                Keyboard.KEY_NUMPAD0,
                "key.categories.daycounter.gui"
        );

        keyBindings[1] = new KeyBinding(
                "key.daycounter.resettime",
                Keyboard.KEY_NUMPAD1,
                "key.categories.daycounter.gui"
        );
        
        keyBindings[2] = new KeyBinding(
                "key.daycounter.pausetime",
                Keyboard.KEY_NUMPAD2,
                "key.categories.daycounter.gui"
        );

        for(int i = 0; i < KEYBINDINGS_COUNT; i++)
            ClientRegistry.registerKeyBinding(keyBindings[i]);
    }
}
