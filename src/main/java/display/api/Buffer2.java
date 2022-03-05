package display.api;

import com.google.common.collect.Range;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.*;

@Getter
@Accessors(fluent = true)
@NonNull
public final class Buffer2<Slot, Animation, Frame> {

    private final Map<Animation, Integer> frames = new HashMap<>();

    private final boolean intervalSupport;
    private final Slot slot;
    private final Timings timings;

    private final Animator<Animation, Frame> animator;
    private final List<Animation> animations;


    public Buffer2(boolean intervalSupport, Slot slot, Timings timings, Animator<Animation, Frame> animator, List<Animation> animations) {
        this.intervalSupport = intervalSupport;
        this.slot = slot;
        this.timings = timings;
        this.animator = animator;
        this.animations = new ArrayList<>(animations);
    }

    private int repeated, totalCycleLengths, ticks;

    public int relativity() {
        return -timings.actualDelay() - totalCycleLengths - totalCycleDelay();
    }

    public boolean inIntervalDelay() {
        return false;
    }

    public Frame peek() {
        return buffer(false);
    }

    public Frame poll() {
        return buffer(true);
    }

    public Frame buffer(boolean poll) {

        List<Frame> frames = new ArrayList<>();

        if (inIntervalDelay()) {
            return null;
        }

        if (inFinalDelay()) {
            animations.forEach(a -> frames.add(animator.animate(a, timings.reversed() ? 0 : frames(a) - 1)));
        }

        int maxFrames = maxFrames();

        for (Animation animation : animations) {

            int f = frames(animation);

            if (inDelay(animation)) {

                frames.add(animator.animate(animation, !timings.reversed() ? 0 : f - 1));

            } else if (inCycle(animation)) {

                int relativeTicks = relativeTicks(animation);

                if (relativeTicks == 0 && f == maxFrames) {
                    totalCycleLengths += maxFrames * timings.interval();
                    ++repeated;
                }

                frames.add(animator.animate(animation, relativeTicks / timings.interval()));

            } else if (inCycleDelay(animation)) {

                frames.add(animator.animate(animation, timings.reversed() ? 0 : f - 1));
            }

        }

        if (poll) {
            ++ticks;
        }

        return null;
    }

    /**
     * Tests if an animation is being delayed. The animation will be delayed when
     * all of the following conditions are met:
     * <p></p>
     * <b>1-</b> The number of times this buffer has repeated is zero
     * <p></p>
     * <b>2-</b> The number of relative ticks is greater than the relativity
     * <p></p>
     * <b>3-</b> The number of relative ticks is less than zero
     * <p></p>
     * <p></p>
     * During this state of the buffer the frame for the provided animation will be
     * null if the buffer supports intervals. If the buffer does not support
     * intervals, the following results will occur:
     * <p></p>
     * Frame# is 0 when {@link Timings#reversed()} is TRUE from {@link #timings()}
     * <p></p>
     * Frame# is {@link #frames(Animation)} when Timings.reversed() is FALSE
     * <p></p>
     * The frame currently provided by the animation will be buffered with the other
     * animation's frames.
     * <p></p>
     * <b>CAUTION:</b> The result of this method for provided animation, will likely
     * be different from the other animations depending on the number of frames the
     *
     * @param animation to test
     * @return if the animation is currently being delayed
     **/
    public boolean inDelay(Animation animation) {
        return repeated == 0 && Range.closedOpen(relativity(), 0).contains(relativeTicks(animation));
    }

    /**
     * Tests if an animation is in a cycle state. The provided animation will be in
     * a cycle when all of the following conditions are met:
     * <p></p>
     * <b>1-</b> The number of times this buffer has repeated is less than
     * {@link Timings#repeats()} from {@link #timings()}
     * <p></p>
     * <b>2-</b> The number of relative ticks is greater than or equal to zero
     * <p></p>
     * <b>3-</b> The number of relative ticks is less than the
     * {@link #cycleLength(Animation)}
     * <p></p>
     * <b>CAUTION:</b> The result of this method for provided animation, will likely
     * be different from the other animations depending on the number of frames the
     * animation has.
     *
     * @param animation to check
     * @return of the animation is currently in a cycle
     */
    public boolean inCycle(Animation animation) {

        return repeated < timings.repeats()
                && Range.closedOpen(0, cycleLength(animation)).contains(relativeTicks(animation));
    }

    /**
     * Tests if an animation is in a cycle delay. The provided animation is in a
     * cycle delay when the following conditions are met:
     * <p></p>
     * <b>1-</b> {@link #ticks()} is greater than
     * {@link Timings#actualDelay()} from {@link #timings()}
     * <p></p>
     * <b>2-</b> {@link #relativeTicks(Animation)} is less than 0
     * <p></p>
     * <b>CAUTION:</b> The result of this method for provided animation, will likely
     * be different from the other animations depending on the number of frames the
     * animation has.
     *
     * @param animation to test
     * @return if the animation is currently in a cycle delay
     */
    public boolean inCycleDelay(Animation animation) {
        return ticks > timings.actualDelay() && relativeTicks(animation) < 0;
    }

    /**
     * Tests if the buffer is in a final delay. The buffer is in a final delay when
     * the following conditions are met:
     * <p></p>
     * <b>1-</b> {@link #timings()}.{@link Timings#isInfinite()} is false
     * <p></p>
     * <b>2-</b> {@link #repeated()} equals
     * {@link Timings#repeats()} from {@link #timings()}
     * <p></p>
     * <b>3-</b> {@link #ticks()} is greater than -{@link #relativity()}
     * <p></p>
     * <b>4-</b> {@link #ticks()} is less than -{@link #relativity()} +
     * {@link Timings#actualFinalDelay()} from {@link #timings()}
     * <p></p>
     *
     * @return if the animation is currently in a cycle delay
     */
    public boolean inFinalDelay() {

        int negRelativity = -relativity();

        return !timings.isInfinite() && repeated == timings.repeats()
                && Range.openClosed(negRelativity, negRelativity + timings.actualFinalDelay()).contains(ticks);
    }

    /**
     * Tests if the buffer is complete. The buffer is complete if the following
     * conditions are met:
     * <p></p>
     * <b>1-</b> {@link Timings#isMaxed()} is true from {@link #timings()}
     * <p></p>
     * <b>2-</b> {@link #ticks()} is greater than or equal to
     * {@link Timings#maxTicks()} from {@link #timings()}
     * <p></p>
     * <b>Or the following</b>
     * <p></p>
     * <b>1-</b> {@link #repeated()} is equal to
     * {@link Timings#repeats()} from {@link #timings()}
     * <p></p>
     * <b>2-</b> {@link #inFinalDelay()} is false
     * <p></p>
     *
     * @return if the animation is currently in a cycle delay
     */
    public boolean isComplete() {
        return (timings.isMaxed() && ticks >= timings.maxTicks()) || repeated == timings.repeats() && !inFinalDelay();
    }

    /**
     * Calculates the relative ticks the animation is in a cycle.
     *<p></p>
     * @param animation to get the relative ticks of
     * @return {@link #ticks()} - {@link #relativity()} -
     * {@link #cycleLength(Animation)}
     */
    public int relativeTicks(Animation animation) {
        return ticks - relativity() - cycleLength(animation);
    }

    /**
     * The total cycle delay of all repeats so far in ticks
     *<p></p>
     * @return {@link #repeated()} *
     * {@link Timings#repeatDelay()} from {@link #timings()}
     */
    public int totalCycleDelay() {
        return repeated * timings.repeatDelay();
    }

    /**
     * Gets the number of frames the animation currently has. The number of frames
     * an animation has can change at the end of each cycle delay.
     *<p></p>
     * @param animation to get the number of frames for
     * @return the number of frames the animation currently has
     */
    public int frames(Animation animation) {

        if (ticks == 0) {
            return frames.compute(animation, (a, f) -> animator.frames(animation));
        }

        return frames.get(animation);
    }

    /**
     * Gets the current cycle length for the provided animation
     *<p></p>
     * @param animation to get the cycle length of
     * @return {@link #frames()} * {@link Timings#interval()} from  {@link #timings()}
     */
    public int cycleLength(Animation animation) {
        return frames(animation) * timings.interval();
    }

    public int maxFrames() {
        return frames(Collections.max(frames.keySet(), Comparator.comparingInt(this::frames)));
    }
}
