function formData(formID) {
    let pair = $("#" + formID).serializeArray();
    let result = {};

    $.map(pair, function (item) {
        result[item['name']] = item['value'];
    });

    return JSON.stringify(result);
}

function postRequestBody(url, body, callback) {

    if (callback == null) {
        callback = () => {};
    }

    $.ajax({
        url: url,
        type: "POST",
        contentType: "application/json",
        data: body,
        dataType: "json",
        success: callback,
    });
}