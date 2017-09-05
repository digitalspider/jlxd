$(document).ready(function () {

	// Search form
    $("#search-form").submit(function (event) {
        event.preventDefault();
        var placeholderEle = $("#containers");
        var postUrl = "/container/search/"+$("#searchTerm").val();
        var templatePath = "template/handlebars/container.html";
        var jsonData = "";

        fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
    });
    
    // Add Server Form
	$("#addServerButton").click(function (event) {
		event.preventDefault();
	    var name = $("#addServerForm #name").val();
	    var hostAndPort = $("#addServerForm #hostAndPort").val();
	    var description = $("#addServerForm #description").val();
	    var postUrl = "/server/create/"+name+"/"+hostAndPort+"/"+description;

	    var placeholderEle = $("#servers");
	    var templatePath = "template/handlebars/server.html";
	    var jsonData = "";

		fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
	});

	$("#addContainerButton").click(function (event) {
		event.preventDefault();
	    var name = $("#addContainerForm #name").val();
	    var imageAlias = encodeURIComponent($("#addContainerForm #imageAlias").val());
	    var profile = $("#addContainerForm #profile").val();
	    var config = $("#addContainerForm #config").val();
	    var ephemeral = $("#addContainerForm #ephemeral").val();
	    var postUrl = "/container/create/"+name+"/"+imageAlias+"/"+ephemeral+"/"+profile+"/"+config;

	    var placeholderEle = $("#containers");
	    var templatePath = "template/handlebars/container.html";
	    var jsonData = "";

		fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
	});

    // initialise the page
	getAllServers();
	reloadContainers();
});

function reloadContainers() {
    var placeholderEle = $("#containers");
    var postUrl = "/container/search";
    var templatePath = "template/handlebars/container.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}