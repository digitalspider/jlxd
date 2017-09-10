$(document).ready(function () {

    $("#search-form").submit(function (event) {
        event.preventDefault();
        var placeholderEle = $("#images");
        var postUrl = "/image/search/"+$("#searchTerm").val();
        var templatePath = "template/handlebars/image.html";
        var search = {};
        search["searchTerm"] = $("#searchTerm").val();
        var jsonData = JSON.stringify(search);

        fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
    });

    // initialise the page
	getAllServers();
	reloadImages();
});

function reloadImages() {
    var placeholderEle = $("#images");
    var postUrl = "/image/reload";
    var templatePath = "template/handlebars/image.html";
    var search = {};
    search["searchTerm"] = $("#searchTerm").val();
    var jsonData = JSON.stringify(search);

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}
