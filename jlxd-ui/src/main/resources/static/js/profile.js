$(document).ready(function () {

    $("#search-form").submit(function (event) {
        event.preventDefault();
        var placeholderEle = $("#profiles");
        var postUrl = "/profile/search/"+$("#searchTerm").val();
        var templatePath = "template/handlebars/profile.html";
        var search = {};
        search["searchTerm"] = $("#searchTerm").val();
        var jsonData = JSON.stringify(search);

        fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
    });

    // initialise the page
	getAllServers();
	reloadProfiles();
});
