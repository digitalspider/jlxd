$(document).ready(function () {

	// Search form
    $("#search-form").submit(function (event) {
        event.preventDefault();
        var searchTerm = $("#searchTerm").val();
        searchContainers(searchTerm);
    });
    
    // Add Server Form
	$("#addServerButton").click(function (event) {
		event.preventDefault();
		var name = $("#addContainerForm #name").val();
	    var hostAndPort = $("#addServerForm #hostAndPort").val();
	    var description = $("#addServerForm #description").val();
	    addServer(name, hostAndPort, description);
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
	    addContainer(name, imageAlias, profile, config, ephemeral, architecture);
	});

    // initialise the page
	getAllServers();
	searchContainers("");
});

function searchContainers(searchTerm) {
    var placeholderEle = $("#containers");
    var postUrl = "/container/search";
    var templatePath = "template/handlebars/container.html";
    var searchInput = {};
    searchInput["searchTerm"] = searchTerm;
    var jsonData = JSON.stringify(searchInput);

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}

function reloadContainers() {
    var placeholderEle = $("#containers");
    var postUrl = "/container/reload";
    var templatePath = "template/handlebars/container.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}

function addServer(name, hostAndPort, description) {
	var placeholderEle = $("#servers");
	var postUrl = "/server/create/"+encodeURIComponent(name)+"/"+hostAndPort+"/"+description;
    var templatePath = "template/handlebars/server.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}

function addContainer(name, imageAlias, profile, config, ephemeral, architecture) {
    var placeholderEle = $("#containers");
	var postUrl = "/container/create";
    var templatePath = "template/handlebars/container.html";
    var addContainerInput = {};
    addContainerInput["name"] = name;
    addContainerInput["imageAlias"] = imageAlias;
    addContainerInput["profile"] = profile;
    addContainerInput["config"] = config;
    addContainerInput["ephemeral"] = ephemeral;
    addContainerInput["architecture"] = architecture;
    var jsonData = JSON.stringify(addContainerInput);

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}