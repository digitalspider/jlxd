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
    var templatePath = "template/handlebars/image.html";
    var searchInput = {};
    searchInput["searchTerm"] = searchTerm;
    var jsonData = JSON.stringify(searchInput);

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}

function reloadImages() {
    var placeholderEle = $("#images");
    var postUrl = "/image/reload";
    var templatePath = "template/handlebars/image.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}
