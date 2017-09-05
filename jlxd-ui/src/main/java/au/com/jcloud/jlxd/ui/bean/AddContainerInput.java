package au.com.jcloud.jlxd.ui.bean;

import au.com.jcloud.lxd.bean.ImageConfig;

public class AddContainerInput extends ImageConfig {
	private String name;
	private String imageOrAlias;

	@Override
	public String toString() {
		return "AddContainerData [name=" + name + ", imageOrAlias=" + imageOrAlias + " super=" + super.toString() + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageOrAlias() {
		return imageOrAlias;
	}

	public void setImageOrAlias(String imageOrAlias) {
		this.imageOrAlias = imageOrAlias;
	}

}
