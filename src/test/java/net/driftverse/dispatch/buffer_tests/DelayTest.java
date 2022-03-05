package net.driftverse.dispatch.buffer_tests;

import display.api.Buffer2;
import display.api.Timings;
import net.driftverse.dispatch.NumAnimator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

public class DelayTest {

    int animation = 10;
    int interval = 2;
    int repeats = 2;
    int delay = 10;
    int repeatDelay = 10;
    int finalDelay = 10;

    Timings timings = Timings.of().delay(delay).repeatDelay(repeatDelay).finalDelay(finalDelay).interval(interval).repeats(repeats).build();

    Buffer2<Integer, Integer, Integer> newBuffer(boolean intervalSupport) {
        return new Buffer2<>(intervalSupport, 0, timings, new NumAnimator(), List.of(animation));
    }

    @Test
    public void inDelay() {
        Buffer2<Integer, Integer, Integer> buffer = newBuffer(false);

        IntStream.range(0, delay).forEach(i -> {
            Assert.assertTrue("Animation was not in a delay", buffer.inDelay(animation));
            buffer.poll();
        });

        Assert.assertTrue("Animation should no longer be in a delay", !buffer.inDelay(animation));
    }

}
