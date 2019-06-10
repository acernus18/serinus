<html lang="zh">
<head>
    <title></title>

    <script src="/webjars/jquery/3.0.0/jquery.min.js"></script>
    <script src="/static/scripts/common.js"></script>

    <script>
        function submit() {
            let form = formDataObject("strategy-form");
            form["alwaysReturn"] = form["alwaysReturn"] === "on";
            console.log(form);

            postRequestBody("/strategy/save", JSON.stringify(form), () => {
                location.reload();
            })
        }
    </script>
</head>
<body>
<form id="strategy-form">
    <label>
        <input type="text" name="product">
    </label>
    <!--private Integer type;-->
    <label>
        <input type="radio" name="type" value="0">
        <input type="radio" name="type" value="1">
        <input type="radio" name="type" value="2">
    </label>

    <!--private Integer presetType;-->
    <label>
        <input type="radio" name="presetType" value="0">
        <input type="radio" name="presetType" value="1">
        <input type="radio" name="presetType" value="2">
    </label>

    <!--private String title;-->
    <label>
        <input type="text" name="title">
    </label>

    <label>
        <input type="number" name="maxCount">
    </label>

    <!--private Date startAt;-->
    <label>
        <input type="datetime-local" name="startAt">
    </label>

    <!--private Date endAt;-->
    <label>
        <input type="datetime-local" name="endAt">
    </label>

    <!--private String filter;-->
    <label>
        <textarea name="filter"></textarea>
    </label>

    <!--private String content;-->
    <label>
        <textarea name="content"></textarea>
    </label>

    <label>
        <input type="checkbox" name="alwaysReturn">
    </label>
</form>

<button onclick="submit()">submit</button>
</body>
</html>
