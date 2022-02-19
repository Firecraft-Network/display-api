package hyleo.animations.displays;

import java.util.List;

import org.bukkit.entity.Player;

import hyleo.animations.api.Animator;
import hyleo.animations.api.Buffer;
import hyleo.animations.api.Display;
import hyleo.animations.displays.Nametag.Tag;
import hyleo.animations.text.TextAnimation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Nametag extends Display<Tag, TextAnimation, Component> {
	private static final Nametag nametag = new Nametag();

	public static enum Tag {
		PREFIX, SUFFIX, LIST, BELOW;
	};

	public static final Nametag instance() {
		return nametag;
	}

	@Override
	public boolean intervalSupport() {
		return true;
	}

	@Override
	public Animator<TextAnimation, Component> animator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldDisplay(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void create(Player player, List<Buffer<Tag, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Player player, List<Buffer<Tag, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy(Player player, List<Buffer<Tag, TextAnimation, Component>> buffers) {
		// TODO Auto-generated method stub
		
	}

}
