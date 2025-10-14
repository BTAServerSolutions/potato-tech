package goldenage.potatotech.mixins;


import goldenage.potatotech.IKeybindings;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(
	value = GameSettings.class,
	remap = false
)
public class GameSettingsMixin implements IKeybindings {
	@Unique
	private final GameSettings thisAs = ((GameSettings)(Object)this);

	@Unique
	public KeyBinding keyOpenSuit = new KeyBinding("key.potatotech.wrenchMode").bind(InputDevice.keyboard, Keyboard.KEY_F);

	@Override
	public KeyBinding potatotech$getWrenchMode() {
		return keyOpenSuit;
	}
}
