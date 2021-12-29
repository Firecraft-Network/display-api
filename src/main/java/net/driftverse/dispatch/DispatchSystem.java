package net.driftverse.dispatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.driftverse.dispatch.api.Dispatcher;
import net.driftverse.dispatch.impl.DispatcherImpl;

public class DispatchSystem extends JavaPlugin {

	private static Plugin plugin;

	private static final Multimap<Plugin, Dispatcher<?, ?>> dispatchers = ArrayListMultimap.create();
	private static final Map<Dispatcher<?, ?>, DispatcherImpl<?, ?>> implementedDispatchers = new HashMap<>();

	public void onLoad() {
		plugin = this;
	}

	public void onEnable() {

	}

	public void onDisable() {
		dispatchers.keySet().forEach(p -> disableAll(plugin, p));
	}

	public static Plugin plugin() {
		return plugin;
	}

	public static void enable(Plugin plugin, Dispatcher<?, ?> dispatcher, boolean async) {
		Validate.notNull(plugin, "Null plugins can not be used to register dispatchers");
		Validate.notNull(dispatcher, "Null dispatchers can not be registered");

		dispatchers.put(plugin, dispatcher);

		DispatcherImpl<?, ?> implemented = new DispatcherImpl<>(plugin, dispatcher);
		implementedDispatchers.put(dispatcher, implemented);

		if (async) {
			implemented.runTaskTimerAsynchronously(DispatchSystem.plugin, 0, 1);
		} else {
			implemented.runTaskTimer(DispatchSystem.plugin, 0, 1);
		}

		Logger logger = plugin().getLogger();
		logger.info("Enabled dispatcher \"" + dispatcher.getClass().getName() + "\" from plugin " + plugin.getName());
	}

	public static void disable(Plugin caller, Dispatcher<?, ?> dispatcher) {

		Validate.notNull(caller, "The plugin disabling a dispatcher (the caller) can not be null");
		Validate.notNull(dispatcher, "Null dispatchers can not be disabled");

		if (!isEnabled(dispatcher)) {
			Logger logger = plugin().getLogger();
			logger.warning(caller.getName()
					+ " tried to disable a dispatcher that has not yet been enabled. Call \"enable()\" first!");
		} else {

			DispatcherImpl<?, ?> implemented = implementedDispatchers.remove(dispatcher);
			implemented.cancel();

			Plugin plugin = implemented.plugin();

			dispatchers.get(plugin).remove(dispatcher);

		}

	}

	public static void disableAll(Plugin caller, Plugin plugin) {
		Validate.notNull(plugin, "Null plugins can not be used to disable dispatchers");
		dispatchers.get(plugin).forEach(d -> disable(caller, d));
	}

	public static Plugin plugin(Dispatcher<?, ?> dispatcher) {
		Validate.notNull(dispatcher, "Null dispatchers can not be used to retrive plugins");
		return !isEnabled(dispatcher) ? null : implementedDispatchers.get(dispatcher).plugin();
	}

	public static boolean isEnabled(Dispatcher<?, ?> dispatcher) {
		Validate.notNull(dispatcher, "Can not check if null dispatcher is enabled");
		return implementedDispatchers.containsKey(dispatcher);
	}

	public static List<Dispatcher<?, ?>> dispatchers(BiFunction<Plugin, Dispatcher<?, ?>, Boolean> filter) {
		Validate.notNull(filter, "Null filters can not be used to retrive dispatchers");
		List<Dispatcher<?, ?>> list = new ArrayList<>();

		for (Plugin plugin : dispatchers.keySet()) {

			for (Dispatcher<?, ?> dispatcher : dispatchers.get(plugin)) {

				if (filter.apply(plugin, dispatcher)) {
					list.add(dispatcher);
				}

			}

		}

		return list;
	}

}
