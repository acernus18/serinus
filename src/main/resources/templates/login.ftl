<html lang="zh">
<head>
    <title>Login</title>
    <script src="/webjars/jquery/3.0.0/jquery.min.js"></script>
    <script src="/static/scripts/common.js"></script>
    <script>
        function submit() {
            login($('#login-form').serialize());
        }
    </script>
</head>


<body>
<form method="POST" id="login-form">
    <h1>登录管理系统</h1>
    <div>
        <label>
            <input type="text" placeholder="请输入用户名" name="username" required=""/>
        </label>
    </div>
    <div>
        <label>
            <input type="password" placeholder="请输入密码" name="password" required=""/>
        </label>
    </div>
    <div>
        <label>
            <input type="checkbox" id="rememberMe" name="rememberMe">记住我
        </label>
    </div>
</form>
<div>
    <button onclick="submit()">登录</button>
</div>
</body>
</html>
