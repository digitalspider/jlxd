$(document).ready(function () {
    // initialise the page
	getServerInfo();
});

function getServerInfo() {
    var placeholderEle = $("#serverinfo");
    var postUrl = "/server/info";
    var templateName = "serverinfo";
    var jsonData = "";

	fire_ajax_submit(postUrl, jsonData, templateName, placeholderEle);
}
