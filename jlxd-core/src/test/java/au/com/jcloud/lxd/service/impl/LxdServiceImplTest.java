package au.com.jcloud.lxd.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.response.ContainerResponse;
import au.com.jcloud.lxd.model.response.ListResponse;
import au.com.jcloud.lxd.service.ILinuxCliService;
import au.com.jcloud.lxd.service.ILxdApiService;

public class LxdServiceImplTest {

	private LxdServiceImpl service;
	private ILxdApiService lxdApiService;
	private ILinuxCliService linuxCliService;
	
	@Before
	public void setup() {
		service = new LxdServiceImpl();
		lxdApiService = spy(new LxdApiServiceImpl());
		linuxCliService = spy(new LinuxCliServiceImpl());
		lxdApiService.setLinuxCliService(linuxCliService);
		service.setLxdApiService(lxdApiService);
	}

	@Test
	public void loadContainers() throws IOException, InterruptedException {
		ListResponse response = new ListResponse();
		response.setStatusCode("200");
		Mockito.doReturn(response).when(linuxCliService).executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers", ListResponse.class);
		Map<String, Container> containers = service.loadContainerMap();
		assertEquals(0, containers.size());
	}
	
	@Test
	public void loadContainers_withData() throws IOException, InterruptedException {
		ListResponse response = new ListResponse();
		response.setStatusCode("200");
		List<String> metadata = new ArrayList<>();
		metadata.add("/1.0/containers/test1");
		metadata.add("/1.0/containers/test2");
		response.setMetadata(metadata);
		Mockito.doReturn(response).when(linuxCliService).executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers", ListResponse.class);
		
		ContainerResponse response1 = new ContainerResponse();
		response1.setStatusCode("200");
		response1.setMetadata(mock(Container.class));
		Mockito.doReturn(response1).when(linuxCliService).executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers/test1", ContainerResponse.class);
		
		ContainerResponse response2 = new ContainerResponse();
		response2.setStatusCode("200");
		response2.setMetadata(mock(Container.class));
		Mockito.doReturn(response2).when(linuxCliService).executeLinuxCmdWithResultJsonObject("curl -s --unix-socket /var/lib/lxd/unix.socket a/1.0/containers/test2", ContainerResponse.class);
		
		Map<String, Container> containers = service.loadContainerMap();
		assertEquals(2, containers.size());
	}

}
