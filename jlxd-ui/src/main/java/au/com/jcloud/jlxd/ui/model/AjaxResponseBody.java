package au.com.jcloud.jlxd.ui.model;

import java.util.Collection;

import au.com.jcloud.lxd.model.Container;

public class AjaxResponseBody {

    String msg;
    Collection<Container> result;
    
    //getters and setters
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Collection<Container> getResult() {
		return result;
	}
	public void setResult(Collection<Container> result) {
		this.result = result;
	}
}
