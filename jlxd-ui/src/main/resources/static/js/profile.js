$(document).ready(function () {

    $("#search-form").submit(function (event) {
        event.preventDefault();
        fire_ajax_submit("/profile/api/search", "template/handlebars/profile.html");
    });

    // Do a default search for all
    $("#searchTerm").val("*");
    $("#bth-search").click();
});

function fire_ajax_submit(postUrl, templatePath) {

    var search = {}
    search["searchTerm"] = $("#searchTerm").val();

    $("#btn-search").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: postUrl,
        data: JSON.stringify(search),
        dataType: 'json',
        cache: false,
        timeout: 60000,
        success: function (data) {

            var json = "<h4>Ajax Response</h4><pre>" + JSON.stringify(data, null, 4) + "</pre>";
            //$('#feedback').html(json);
            var placeholderEle = $("#containers");
            placeholderEle.html("");
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