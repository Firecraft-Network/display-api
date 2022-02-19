package hyleo.animations.displays;

import java.util.List;

import org.bukkit.entity.Player;

import hyleo.animations.api.Animator;
import hyleo.animations.api.Buffer;
import hyleo.animations.api.Display;
import hyleo.animations.text.TextAnimation;
import hyleo.animations.text.TextAnimator;
import net.kyori.adventure.text.Component;

public class Tablist extends Display<Integer, TextAnimation, Component> {

	private static final Tablist tablist = new Tablist();

	private Tablist() {
	}

	public static final Tablist istance() {
		return tablist;
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
	public void create(Player player, List<Buffer<Integer, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Player player, List<Buffer<Integer, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy(Player player, List<Buffer<Integer, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub

	}

}
