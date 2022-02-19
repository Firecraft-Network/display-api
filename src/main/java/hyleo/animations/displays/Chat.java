package hyleo.animations.displays;

import java.util.List;

import org.bukkit.entity.Player;

import hyleo.animations.api.Animator;
import hyleo.animations.api.Buffer;
import hyleo.animations.api.Display;
import hyleo.animations.text.TextAnimation;
import hyleo.animations.text.TextAnimator;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;

public class Chat extends Display<ChatMessageType, TextAnimation, Component> {

	private static final Chat chat = new Chat();

	private Chat() {
	}
	
	public static final Chat instance() {
		return chat;
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
	public void create(Player player, List<Buffer<ChatMessageType, TextAnimation, Component>> buffers) {

		buffers.forEach(b -> b.poll()); // Skip to update cycle

	}

	@Override
	public void update(Player player, List<Buffer<ChatMessageType, TextAnimation, Component>> buffers) {
		for (Buffer<ChatMessageType, TextAnimation, Component> buffer : buffers) {
			ChatMessageType type = buffer.slot();

			Component text = buffer.poll();

			if (type == ChatMessageType.ACTION_BAR) {
				player.sendActionBar(text);
			} else if (type == ChatMessageType.CHAT) {
				player.sendMessage(text);
			} else if (type == ChatMessageType.SYSTEM) {
				player.sendMessage(text, MessageType.SYSTEM);
			}

		}
	}

	@Override
	public void destroy(Player player, List<Buffer<ChatMessageType, TextAnimation, Component>> buffers) {
		for (Buffer<ChatMessageType, TextAnimation, Component> buffer : buffers) {
			ChatMessageType type = buffer.slot();

			if (type == ChatMessageType.ACTION_BAR) {
				player.sendActionBar(Component.text(""));
			}
		}

	}

}
