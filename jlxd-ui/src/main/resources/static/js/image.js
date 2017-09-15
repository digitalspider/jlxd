$(document).ready(function () {
	// Search form
    $("#search-form").submit(function (event) {
        event.preventDefault();
        var searchTerm = $("#searchTerm").val();
        searchImages(searchTerm);
    });

    // initialise the page
	getAllServers();
	searchImages("");
});

function searchImages(searchTerm) {
    var placeholderEle = $("#images");
    var postUrl = "/image/search";
    var templateName = "image";
    var searchInput = {};
    searchInput["searchTerm"] = searchTerm;
    var jsonData = JSON.stringify(searchInput);

	fire_ajax_submit(postUrl, jsonData, templateName, placeholderEle);
}

function reloadImages() {
    var placeholderEle = $("#images");
    var postUrl = "/image/reload";
    var templateName = "image";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templateName, placeholderEle);
}

function toggleEditState(event, element, containerName) {
	event.preventDefault();
	var postUrl = "/image/save/"+containerName;
	var eleId = "#"+element.id;
	var rowId = "#row_"+containerName;
	console.log(eleId+" "+rowId);
	if (eleId == "#editContainer") {
		$(rowId).css("background-color", "lightgrey");
		$(rowId).find("#desc").html("<input type='text' name='decription' id='editDesc' value='"+$(rowId).find("#desc").text()+"'></input>");
	} else {
		$(rowId).css("background-color", "");
		$(rowId).find("#desc").text($(rowId).find("#editDesc").val());
	}
	$(rowId).find("#editContainer").toggle();
	$(rowId).find("#saveContainer").toggle();
}
