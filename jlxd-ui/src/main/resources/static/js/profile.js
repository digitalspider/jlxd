$(document).ready(function () {

    $("#search-form").submit(function (event) {
        event.preventDefault();
        var placeholderEle = $("#profiles");
        var postUrl = "/profile/search/";
        var templatePath = "template/handlebars/profile.html"
        fire_ajax_submit(postUrl, templatePath, placeholderEle);
    });

    // initialise the page
	getAllServers();
	reloadProfiles();
});
