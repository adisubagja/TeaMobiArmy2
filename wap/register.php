<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Trang đăng kí</title>
    </head>
    <body>
        <h1>Trang dang kí thành viên</h1>
        <form action="regaction.php" method="POST">
            <table cellpadding="0" cellspacing="0" border="1">
                <tr>
                    <td>
                        Tên đăng nhập: 
                    </td>
                    <td>
                        <input type="text" name="txtUsername" size="50" />
                    </td>
                </tr>
                <tr>
                    <td>
                        Mật khẩu :
                    </td>
                    <td>
                        <input type="password" name="txtPassword" size="50" />
                    </td>
                </tr>
            <input type="submit" value="Đăng kí" />
            <input type="reset" value="Nhập lại" />
        </form>
    </body>
</html>