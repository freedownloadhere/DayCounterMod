package freedownloadhere.tutorialmod.proxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy
{
    public static KeyBinding[] keyBindings;
    public static final int KEYBINDINGS_COUNT = 2;

    @Override
    public void init()
    {
        keyBindings = new KeyBinding[KEYBINDINGS_COUNT];

        keyBindings[0] = new KeyBinding(
                "key.tutorialmod.togglegui",
                Keyboard.KEY_NUMPAD0,
                "key.categories.tutorialmod.gui"
        );

        keyBindings[1] = new KeyBinding(
                "key.tutorialmod.resettime",
                Keyboard.KEY_NUMPAD1,
                "key.categories.tutorialmod.gui"
        );

        for(int i = 0; i < KEYBINDINGS_COUNT; i++)
            ClientRegistry.registerKeyBinding(keyBindings[i]);
    }
}
