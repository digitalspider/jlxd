package au.com.jcloud.lxd.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import au.com.jcloud.lxd.bean.LxdServerCredential;
import au.com.jcloud.lxd.model.Certificate;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.Network;
import au.com.jcloud.lxd.model.Profile;
import au.com.jcloud.lxd.service.impl.AbstractLxdService;

public class AbstractLxdServiceTest {

	private AbstractLxdService service;

	@Before
	public void setup() {
		service = spy(AbstractLxdService.class);
	}

	@Test
	public void testRemoteHostAndPort() {
		LxdServerCredential credential = new LxdServerCredential();
		credential.setRemoteHostAndPort("test");
		service.setLxdServerCredential(credential);
		assertEquals("test", service.getLxdServerCredential().getRemoteHostAndPort());
	}

	@Test
	public void getContainers() throws IOException, InterruptedException {
		Map<String, Container> containerMap = new HashMap<>();
		Container container = mock(Container.class);
		containerMap.put("test", container);
		doReturn(containerMap).when(service).loadContainers();
		Collection<Container> result = service.getContainers();
		assertEquals(container, result.iterator().next());
		assertEquals(container, service.getContainer("test"));
	}

	@Test
	public void getContainers_shouldHandleException() throws IOException, InterruptedException {
		Map<String, Container> containerMap = new HashMap<>();
		Container container = mock(Container.class);
		containerMap.put("test", container);
		doThrow(new RuntimeException("error")).when(service).loadContainers();
		Collection<Container> result = service.getContainers();
		assertEquals(0, result.size());
	}

	@Test
	public void getImages() throws IOException, InterruptedException {
		Map<String, Image> imageMap = new HashMap<>();
		Image image = mock(Image.class);
		imageMap.put("test", image);
		doReturn(imageMap).when(service).loadImages();
		Collection<Image> result = service.getImages();
		assertEquals(image, result.iterator().next());
		assertEquals(image, service.getImage("test"));
	}

	@Test
	public void getImages_shouldHandleException() throws IOException, InterruptedException {
		Map<String, Image> imageMap = new HashMap<>();
		Image image = mock(Image.class);
		imageMap.put("test", image);
		doThrow(new RuntimeException("error")).when(service).loadImages();
		Collection<Image> result = service.getImages();
		assertEquals(0, result.size());
	}

	@Test
	public void getNetworks() throws IOException, InterruptedException {
		Map<String, Network> networkMap = new HashMap<>();
		Network network = mock(Network.class);
		networkMap.put("test", network);
		doReturn(networkMap).when(service).loadNetworks();
		Collection<Network> result = service.getNetworks();
		assertEquals(network, result.iterator().next());
		assertEquals(network, service.getNetwork("test"));
	}

	@Test
	public void getNetworks_shouldHandleException() throws IOException, InterruptedException {
		Map<String, Network> networkMap = new HashMap<>();
		Network network = mock(Network.class);
		networkMap.put("test", network);
		doThrow(new RuntimeException("error")).when(service).loadNetworks();
		Collection<Network> result = service.getNetworks();
		assertEquals(0, result.size());
	}

	@Test
	public void getProfiles() throws IOException, InterruptedException {
		Map<String, Profile> profileMap = new HashMap<>();
		Profile profile = mock(Profile.class);
		profileMap.put("test", profile);
		doReturn(profileMap).when(service).loadProfiles();
		Collection<Profile> result = service.getProfiles();
		assertEquals(profile, result.iterator().next());
		assertEquals(profile, service.getProfile("test"));
	}

	@Test
	public void getProfiles_shouldHandleException() throws IOException, InterruptedException {
		Map<String, Profile> profileMap = new HashMap<>();
		Profile profile = mock(Profile.class);
		profileMap.put("test", profile);
		doThrow(new RuntimeException("error")).when(service).loadProfiles();
		Collection<Profile> result = service.getProfiles();
		assertEquals(0, result.size());
	}

	@Test
	public void getCertificates() throws IOException, InterruptedException {
		Map<String, Certificate> certificateMap = new HashMap<>();
		Certificate certificate = mock(Certificate.class);
		certificateMap.put("test", certificate);
		doReturn(certificateMap).when(service).loadCertificates();
		Collection<Certificate> result = service.getCertificates();
		assertEquals(certificate, result.iterator().next());
		assertEquals(certificate, service.getCertificate("test"));
	}

	@Test
	public void getCertificates_shouldHandleException() throws IOException, InterruptedException {
		Map<String, Certificate> certificateMap = new HashMap<>();
		Certificate certificate = mock(Certificate.class);
		certificateMap.put("test", certificate);
		doThrow(new RuntimeException("error")).when(service).loadCertificates();
		Collection<Certificate> result = service.getCertificates();
		assertEquals(0, result.size());
	}
}
