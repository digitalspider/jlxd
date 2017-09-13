$(document).ready(function () {
	Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {
	    switch (operator) {
	        case '==':
	            return (v1 == v2) ? true : false;
	        case '===':
	            return (v1 === v2) ? true : false;
	        case '!=':
	            return (v1 != v2) ? true : false;
	        case '!==':
	            return (v1 !== v2) ? true : false;
	        case '<':
	            return (v1 < v2) ? true : false;
	        case '<=':
	            return (v1 <= v2) ? true : false;
	        case '>':
	            return (v1 > v2) ? true : false;
	        case '>=':
	            return (v1 >= v2) ? true : false;
	        case '&&':
	            return (v1 && v2) ? true : false;
	        case '||':
	            return (v1 || v2) ? true : false;
	        default:
	            return false;
	    }
	});
});

function getAllServers() {
    var placeholderEle = $("#servers");
    var postUrl = "/server";
    var templatePath = "template/handlebars/server.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}

function selectServer(name) {
    var placeholderEle = $("#servers");
    var postUrl = "/server/"+name;
    var templatePath = "template/handlebars/server.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
	reloadContainers();
}

function removeServer(name) {
    var placeholderEle = $("#servers");
    var postUrl = "/server/delete/"+name;
    var templatePath = "template/handlebars/server.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
	return false;
}

function ajaxPost(event, element, postUrl) {
	event.preventDefault();
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

function fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle) {
    placeholderEle.html("");
	$("#feedback").html("");
    $("#btn-search").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: postUrl,
        data: jsonData,
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


/**
 * Handlebars: Load both the templatePath and the dataUrl and place the results into the placeholderEle
 */
function loadTemplatedContent(templatePath, dataUrl, placeholderEle) {
  $.get(templatePath, function (data) {
    var template = Handlebars.compile(data);
    $.get(dataUrl,function(jsonData,status,xhr) {
      var html = template(jsonData);
      placeholderEle.append(html);
    });
  });
}

/**
 * Handlebars: Load the templatePath and merge in the jsonData and place the results into the placeholderEle
 */
function injectTemplatedContent(templatePath, jsonData, placeholderEle) {
  $.get(templatePath, function (data) {
    var template = Handlebars.compile(data);
    var html = template(jsonData);
    placeholderEle.append(html);
  });
}
