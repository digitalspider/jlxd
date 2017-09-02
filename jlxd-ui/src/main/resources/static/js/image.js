$(document).ready(function () {

    $("#search-form").submit(function (event) {
        event.preventDefault();
        var placeholderEle = $("#images");
        var postUrl = "/image/search/";
        var templatePath = "template/handlebars/image.html"
        fire_ajax_submit(postUrl, templatePath, placeholderEle);
    });

    // initialise the page
	getAllServers();
	reloadImages();
});
