/**
 * Load both the templatePath and the dataUrl and place the results into the placeholderEle
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
 * Load the templatePath and merge in the jsonData and place the results into the placeholderEle
 */
function injectTemplatedContent(templatePath, jsonData, placeholderEle) {
  $.get(templatePath, function (data) {
    var template = Handlebars.compile(data);
    var html = template(jsonData);
    placeholderEle.append(html);
  });
}
