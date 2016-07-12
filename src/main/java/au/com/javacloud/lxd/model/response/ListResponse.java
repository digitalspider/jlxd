package au.com.javacloud.lxd.model.response;

import java.util.List;

public class ListResponse extends ResponseBase {
	private List<String> metadata;

	@Override
	public List<String> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<String> metadata) {
		this.metadata = metadata;
	}

}
