package hyleo.animations.displays;

import java.util.List;

import org.bukkit.entity.Player;

import hyleo.animations.api.Animator;
import hyleo.animations.api.Buffer;
import hyleo.animations.api.Display;
import hyleo.animations.text.TextAnimation;
import hyleo.animations.text.TextAnimator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Bar extends Display<BossBar, TextAnimation, Component> {

	private static final Bar bar = new Bar();

	public static final Bar instance() {
		return bar;
	}

	@Override
	public boolean intervalSupport() {
		return true;
	}

	@Override
	public Animator<TextAnimation, Component> animator() {
		return new TextAnimator();
	}

	@Override
	public boolean shouldDisplay(Player player) {
		return true;
	}

	@Override
	public void create(Player player, List<Buffer<BossBar, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Player player, List<Buffer<BossBar, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy(Player player, List<Buffer<BossBar, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub

	}

}
