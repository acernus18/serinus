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

    let request = new XMLHttpRequest();

    request.open("POST", url);
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(body));
    request.onloadend = callback;
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

function serialize(form) {
    if (!form || form.nodeName !== "FORM") {
        return {};
    }

    let result = {};
    for (let i = 0; i < form.elements.length; i++) {
        // console.log(form.elements[i]);
        if (form.elements[i].name === "") {
            continue;
        }
        switch (form.elements[i].nodeName) {
            case 'INPUT':
                switch (form.elements[i].type) {
                    case 'text':
                    case 'tel':
                    case 'email':
                    case 'hidden':
                    case 'password':
                    case 'button':
                    case 'reset':
                    case 'submit':
                        // result.push(form.elements[i].name + "=" + encodeURIComponent(form.elements[i].value));
                        result[form.elements[i].name] = form.elements[i].value;
                        break;
                    case 'checkbox':
                    case 'radio':
                        if (form.elements[i].checked) {
                            // result.push(form.elements[i].name + "=" + encodeURIComponent(form.elements[i].value));
                            result[form.elements[i].name] = form.elements[i].value;
                        }
                        break;
                }
                break;
            case 'file':
                break;
            case 'TEXTAREA':
                // result.push(form.elements[i].name + "=" + encodeURIComponent(form.elements[i].value));
                result[form.elements[i].name] = form.elements[i].value;
                break;
            case 'SELECT':
                switch (form.elements[i].type) {
                    case 'select-one':
                        // result.push(form.elements[i].name + "=" + encodeURIComponent(form.elements[i].value));
                        result[form.elements[i].name] = form.elements[i].value;
                        break;
                    case 'select-multiple':
                        for (let j = 0; j < form.elements[i].options.length; j++) {
                            if (form.elements[i].options[j].selected) {
                                // result.push(form.elements[i].name + "=" + encodeURIComponent(form.elements[i].options[j].value));
                                result[form.elements[i].name] = form.elements[i].options[j].value;
                            }
                        }
                        break;
                }
                break;
            case 'BUTTON':
                break;
        }
    }
    return result;
}