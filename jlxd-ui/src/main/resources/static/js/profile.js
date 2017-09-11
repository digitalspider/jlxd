$(document).ready(function () {
	// Search form
    $("#search-form").submit(function (event) {
        event.preventDefault();
        var searchTerm = $("#searchTerm").val();
        searchProfiles(searchTerm);
    });

    // initialise the page
	getAllServers();
	searchProfiles("");
});

function searchProfiles(searchTerm) {
    var placeholderEle = $("#profiles");
    var postUrl = "/profile/search";
    var templatePath = "template/handlebars/profile.html";
    var searchInput = {};
    searchInput["searchTerm"] = searchTerm;
    var jsonData = JSON.stringify(searchInput);

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}

function reloadProfiles() {
    var placeholderEle = $("#profiles");
    var postUrl = "/profile/reload";
    var templatePath = "template/handlebars/profile.html";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templatePath, placeholderEle);
}
