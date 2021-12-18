package net.driftverse.dispatch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.Iterables;

public class Util {

	@SuppressWarnings("unchecked")
	public static Queue<List<SynthesizerImpl<?, ?>>> copy(List<List<SynthesizerImpl<?, ?>>> synthesizers) {
		@SuppressWarnings("rawtypes")

		Queue queue = new LinkedList<>();

		synthesizers.forEach(
				(g) -> queue.add(g.stream().map((s) -> SerializationUtils.clone(s)).collect(Collectors.toList())));

		return queue;

	}

	@SafeVarargs
	public static <T> Supplier<T> compilation(T... values) {
		Iterator<T> iterator = Iterables.cycle(List.of(values)).iterator();
		return () -> iterator.next();
	}
}
