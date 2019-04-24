<html lang="en">
<head>
    <title>System</title>
</head>

<body>
<div>
<table>
    <thead>
        <tr>
            <th>Principal</th>
            <th>Credential</th>
            <th>Username</th>
            <th>Email</th>
        </tr>
    </thead>
    <tbody>

        <#list allUsers as user>
            <tr>
                <td>${user.principal}</td>
                <td>${user.credential}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
            </tr>
        </#list>
    </tbody>
</table>
</div>
<hr />
<div>
    <#list rolesMapping as role, users>
        <div>
            <h3>${role}</h3>
            <form method="POST" action="/system/permission/${role}/add">
                <label>principal
                    <input name="principal">
                </label>

                <label>level
                    <input name="level">
                </label>
                <input type="submit" value="Submit">
            </form>
            <ul>
                <#list users as user>
                    <li>
                        ${user.name}
                        <a href="/system/permission/delete/${user.name}/${role}">delete</a>
                    </li>
                </#list>
            </ul>
        </div>
    </#list>
</div>
<hr />
<div>
    <form method="POST" action="/system/user/add">
        <label>Principal
            <input name="principal">
        </label>

        <label>Credential
            <input name="credential">
        </label>

        <label>Username
            <input name="name">
        </label>

        <label>Email
            <input name="email">
        </label>
        <input type="submit" value="Submit">
    </form>
</div>

<div>
    <form method="POST" action="/system/role/add">
        <label>Role Name
            <input name="roleName">
        </label>

        <input type="submit" value="Submit">
    </form>
</div>

</body>
</html>
