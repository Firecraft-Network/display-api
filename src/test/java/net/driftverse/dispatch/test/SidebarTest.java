package net.driftverse.dispatch.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

public class SidebarTest {

	ServerMock server;

	@Before
	public void setup() {
		server = MockBukkit.getOrCreateMock();
		Assert.assertTrue("Bukkit Server is not actively mocked", MockBukkit.isMocked());
	}

	@After
	public void tearDown() {
		MockBukkit.unmock();
		Assert.assertFalse("Failed to unmock Bukkit Server", MockBukkit.isMocked());
	}

}
