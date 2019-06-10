function formDataString(formID) {
    let pair = $("#" + formID).serializeArray();
    let result = {};

    $.map(pair, function (item) {
        result[item['name']] = item['value'];
    });

    return JSON.stringify(result);
}

function formDataObject(formID) {
    let pair = $("#" + formID).serializeArray();
    let result = {};

    $.map(pair, function (item) {
        result[item['name']] = item['value'];
    });

    return result;
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

function login(parameter) {
    $.ajax({
        url: "/passport/login",
        type: "POST",
        data: parameter,
        dataType: "json",
        success: (data) => {
            console.log(data);

            if (data["status"] === 0) {
                alert("Login Success");
                location.assign("/index");
            } else {
                alert("Login Fail" + data["message"]);
                location.reload();
            }
        },
    });
}

function logout() {
    $.ajax({
        url: "/passport/logout",
        type: "GET",
        dataType: "json",
        success: (data) => {
            console.log(data);
            location.assign("/index");
        },
    });
}