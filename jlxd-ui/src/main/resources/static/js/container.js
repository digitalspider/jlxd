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
		var name = $("#addContainerForm #name").val();
	    var hostAndPort = $("#addServerForm #hostAndPort").val();
	    var description = $("#addServerForm #description").val();
	    var postUrl = "/server/create/"+encodeURIComponent(name)+"/"+hostAndPort+"/"+description;

	    var placeholderEle = $("#servers");
	    var templatePath = "template/handlebars/server.html";
	    var jsonData = "";

		fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
	});

	$("#addContainerButton").click(function (event) {
		event.preventDefault();
	    var name = $("#addContainerForm #name").val();
	    var imageAlias = $("#addContainerForm #imageAlias").val();
	    var profile = $("#addContainerForm #profile").val();
	    var config = $("#addContainerForm #config").val();
	    var ephemeral = $("#addContainerForm #ephemeral").val();
	    if ("on" == ephemeral) {
	    	ephemeral = "true";
	    } else {
	    	ephemeral = "false";
	    }
	    var architecture = $("#addContainerForm #architecture").val();
	    var postUrl = "/container/create";
        var addContainerInput = {};
        addContainerInput["name"] = name;
        addContainerInput["imageAlias"] = imageAlias;
        addContainerInput["profile"] = profile;
        addContainerInput["config"] = config;
        addContainerInput["ephemeral"] = ephemeral;
        addContainerInput["architecture"] = architecture;

	    var placeholderEle = $("#containers");
	    var templatePath = "template/handlebars/container.html";
	    var jsonData = JSON.stringify(addContainerInput);

		fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
	});

    // initialise the page
	getAllServers();
	reloadContainers();
});

function reloadContainers() {
    var placeholderEle = $("#containers");
    var postUrl = "/container/reload";
    var templatePath = "template/handlebars/container.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}