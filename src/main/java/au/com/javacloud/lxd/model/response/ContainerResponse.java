package au.com.javacloud.lxd.model.response;

import au.com.javacloud.lxd.model.Container;

public class ContainerResponse extends ResponseBase {
	private Container metadata;

	@Override
	public Container getMetadata() {
		return metadata;
	}

	public void setMetadata(Container metadata) {
		this.metadata = metadata;
	}

}
