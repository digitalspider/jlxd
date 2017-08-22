package au.com.jcloud.jlxd.ui;

import org.hibernate.validator.constraints.NotBlank;

public class SearchCriteria {

    @NotBlank(message = "searchTerm can't empty!")
    private String searchTerm;
    
    @Override
	public String toString() {
		return "SearchCriteria [searchTerm=" + searchTerm + "]";
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

}
