package net.driftverse.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import display.text.Palette;
import net.kyori.adventure.text.format.TextColor;

public class PaletteTest {

	int value = 10; // You can change this
	int depth = 9;// You can change this

	/** DONT TOUCH START **/
	final int valueMax = value + (depth * value);
	final int distance = (valueMax - value) / depth;

	TextColor color1 = TextColor.color(value, 0, 0);
	TextColor color2 = TextColor.color(valueMax, 0, 0);

	/** DONT TOUCH END **/

	@Test
	public void distance() {

		Assert.assertEquals(distance, Palette.distance(depth, color1, color2, c -> c.red()));

	}

	@Test
	public void direction() {

		Assert.assertEquals(1, Palette.direction(color1, color2, c -> c.red()));

		Assert.assertEquals(0, Palette.direction(color1, color1, c -> c.red()));

		Assert.assertEquals(-1, Palette.direction(color2, color1, c -> c.red()));

	}

	@Test
	public void color() {

		for (int i = 0; i < depth; ++i) {

			Assert.assertEquals(value + i * distance, Palette.color(i, depth, color1, color2, c -> c.red()));

		}

	}

	@Test
	public void multiColor() {

		for (int i = 0; i < depth; ++i) {
			Assert.assertEquals("Failed on first transition. Color:" + i, 0, Palette.firstColor(depth, i));
			Assert.assertEquals("Failed on first transition. Color:" + i, 1, Palette.secondColor(3, depth, i));
		}

		for (int i = depth; i < depth * 2; ++i) {
			Assert.assertEquals("Failed on second transition. Color:" + i, 1, Palette.firstColor(depth, i));
			Assert.assertEquals("Failed on second transition. Color:" + i, 2, Palette.secondColor(3, depth, i));
		}

		for (int i = depth * 2; i < depth * 3; ++i) {
			Assert.assertEquals("Failed on third transition. Color:" + i, 2, Palette.firstColor(depth, i));
			Assert.assertEquals("Failed on third transition. Color:" + i, 0, Palette.secondColor(3, depth, i));
		}
	}

	// @Test
	public void integration() {

		List<TextColor> colors = new ArrayList<>();

		int depth = 256; // All Possible single channel RGB values

		TextColor red = TextColor.color(depth - 1, 0, 0);
		TextColor green = TextColor.color(0, depth - 1, 0);
		TextColor blue = TextColor.color(0, 0, depth - 1);

		IntStream.range(0, depth).forEach(i -> colors.add(TextColor.color(255 - i, i, 0))); // RED TO BLUE
		IntStream.range(0, depth).forEach(i -> colors.add(TextColor.color(0, 255 - i, i))); // BLUE TO GREEN
		IntStream.range(0, depth).forEach(i -> colors.add(TextColor.color(i, 0, 255 - i))); // GREEN TO RED

		Palette palette = Palette.builder().depth(depth).colors(List.of(red, green, blue)).build();

		for (int i = 0; i < depth * 3; ++i) {

			TextColor color = palette.color(i);

			Assert.assertEquals("Wrong red value", colors.get(i).red(), color.red());
			Assert.assertEquals("Wrong green value", colors.get(i).green(), color.green());
			Assert.assertEquals("Wrong blue value", colors.get(i).blue(), color.blue());

		}

	}

}
