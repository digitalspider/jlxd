$(document).ready(function () {

	// Search form
    $("#search-form").submit(function (event) {
        event.preventDefault();
        var placeholderEle = $("#containers");
        var postUrl = "/container/search/"+$("#searchTerm").val();
        var templatePath = "template/handlebars/container.html"
        fire_ajax_submit(postUrl, templatePath, placeholderEle);
    });
    
    // Add Server Form
	$("#addServerButton").click(function (event) {
		event.preventDefault();
	    var name = $("#addServerForm #name").val();
	    var hostAndPort = $("#addServerForm #hostAndPort").val();
	    var description = $("#addServerForm #description").val();
	    var postUrl = "/server/add/"+name+"/"+hostAndPort+"/"+description;

	    var placeholderEle = $("#servers");
	    var templatePath = "template/handlebars/server.html";

		fire_ajax_submit(postUrl, templatePath, placeholderEle);
	});
	
    // initialise the page
	getAllServers();
	reloadContainers();
});
