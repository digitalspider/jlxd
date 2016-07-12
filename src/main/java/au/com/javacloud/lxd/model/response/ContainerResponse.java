package au.com.javacloud.lxd.model.response;

import au.com.javacloud.lxd.model.Container;

/**
 * Created by david.vittor on 12/07/16.
 */
public class ContainerResponse extends AbstractResponse {
	private Container metadata;

	@Override
	public Container getMetadata() {
		return metadata;
	}

	public void setMetadata(Container metadata) {
		this.metadata = metadata;
	}

}
