package au.com.jcloud.lxd.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.response.ContainerResponse;
import au.com.jcloud.lxd.model.response.ListResponse;
import au.com.jcloud.lxd.util.LinuxUtil;

@RunWith(PowerMockRunner.class)
public class LxdServiceImplTest {

	private LxdServiceImpl service;

	@Before
	public void setup() {
		service = spy(LxdServiceImpl.class);
	}

	@PrepareForTest(LinuxUtil.class)
	@Test
	public void loadContainers() throws IOException, InterruptedException {
		PowerMockito.mockStatic(LinuxUtil.class);
		ListResponse response = new ListResponse();
		response.setStatusCode("200");
		Mockito.when(LinuxUtil.executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers", ListResponse.class)).thenReturn(response);
		Map<String, Container> containers = service.loadContainers();
		assertEquals(0, containers.size());
	}
	
	@PrepareForTest(LinuxUtil.class)
	@Test
	public void loadContainers_withData() throws IOException, InterruptedException {
		PowerMockito.mockStatic(LinuxUtil.class);
		ListResponse response = new ListResponse();
		response.setStatusCode("200");
		List<String> metadata = new ArrayList<>();
		metadata.add("/1.0/containers/test1");
		metadata.add("/1.0/containers/test2");
		response.setMetadata(metadata);
		Mockito.when(LinuxUtil.executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers", ListResponse.class)).thenReturn(response);
		
		ContainerResponse response1 = new ContainerResponse();
		response1.setStatusCode("200");
		response1.setMetadata(mock(Container.class));
		Mockito.when(LinuxUtil.executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers/test1", ContainerResponse.class)).thenReturn(response1);
		
		ContainerResponse response2 = new ContainerResponse();
		response2.setStatusCode("200");
		response2.setMetadata(mock(Container.class));
		Mockito.when(LinuxUtil.executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers/test2", ContainerResponse.class)).thenReturn(response2);
		
		Map<String, Container> containers = service.loadContainers();
		assertEquals(2, containers.size());
	}

}
