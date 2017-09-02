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

function getAllServers() {
    var placeholderEle = $("#servers");
    var postUrl = "/server";
    var templatePath = "template/handlebars/server.html";

	fire_ajax_submit(postUrl, templatePath, placeholderEle);
}

function reloadContainers() {
    var placeholderEle = $("#containers");
    var postUrl = "/container/search";
    var templatePath = "template/handlebars/container.html";

	fire_ajax_submit(postUrl, templatePath, placeholderEle);
}

function selectServer(name) {
    var placeholderEle = $("#servers");
    var postUrl = "/server/"+name;
    var templatePath = "template/handlebars/server.html";

	fire_ajax_submit(postUrl, templatePath, placeholderEle);
	reloadContainers();
}

function removeServer(name) {
    var placeholderEle = $("#servers");
    var postUrl = "/server/delete/"+name;
    var templatePath = "template/handlebars/server.html";

	fire_ajax_submit(postUrl, templatePath, placeholderEle);
	return false;
}

function ajaxPost(postUrl) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: postUrl,
        dataType: 'json',
        cache: false,
        timeout: 60000,
        success: function (data) {
            console.log("SUCCESS : ", data);
        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });
}

function fire_ajax_submit(postUrl, templatePath, placeholderEle) {
    placeholderEle.html("");
	$("#feedback").html("");
    $("#btn-search").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: postUrl,
        dataType: 'json',
        cache: false,
        timeout: 60000,
        success: function (data) {

            var json = "<h4>Ajax Response</h4><pre>" + JSON.stringify(data, null, 4) + "</pre>";
            //$('#feedback').html(json);
            if (data && data.result) {
	            injectTemplatedContent(templatePath,data.result,placeholderEle);
            }

            console.log("SUCCESS : ", data);
            $("#btn-search").prop("disabled", false);

        },
        error: function (e) {

            var json = "<h4>Ajax Response</h4><pre>" + e.responseText + "</pre>";
            $('#feedback').html(json);

            console.log("ERROR : ", e);
            $("#btn-search").prop("disabled", false);

        }
    });

}