package au.com.javacloud.lxd.model.response;

import au.com.javacloud.lxd.model.Image;

public class ImageResponse extends ResponseBase {
	private Image metadata;

	@Override
	public Image getMetadata() {
		return metadata;
	}

	public void setMetadata(Image metadata) {
		this.metadata = metadata;
	}

}
