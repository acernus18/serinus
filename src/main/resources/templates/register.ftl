<html lang="en">
<head>
    <title>Login</title>

    <script src="/webjars/jquery/3.0.0/jquery.min.js"></script>
    <script src="/static/scripts/common.js"></script>
    <script>
        function addUser() {
            postRequestBody("/permission/user/add", formDataString("user-form"), data => {
                console.log(data);
                location.assign("/index");
            })
        }
    </script>
</head>

<body>
<form id="user-form" method="post">
    <div>
        <label for="principal">用户名: <span class="required">*</span></label>
        <div>
            <input type="text" name="principal" id="principal" required="required" placeholder="请输入用户名"/>
        </div>
    </div>
    <div>
        <label for="credential">密码: <span class="required">*</span></label>
        <div>
            <input type="password" id="credential" name="credential" required="required" placeholder="请输入密码 6位以上"/>
        </div>
    </div>
    <div>
        <label for="nickname">昵称:</label>
        <div>
            <input type="text" name="nickname" id="nickname" placeholder="请输入昵称"/>
        </div>
    </div>
    <div>
        <label for="mobile">手机:</label>
        <div>
            <input type="tel" name="mobile" id="mobile" data-validate-length-range="8,20" placeholder="请输入手机号"/>
        </div>
    </div>
    <div>
        <label for="email">邮箱:</label>
        <div>
            <input type="email" name="email" id="email" placeholder="请输入邮箱"/>
        </div>
    </div>
</form>
<button onclick="addUser()">sb</button>
</body>