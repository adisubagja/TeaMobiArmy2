<?php
    // Nếu không phải là sự kiện đăng ký thì không xử lý
    if(!isset($_POST['txtUsername']))
        die('');

    // Nhúng file kết nối với database
    include('conn.php');

    // Khai báo utf-8 để hiển thị được tiếng việt
    header('Content-Type: text/html; charset=UTF-8');

    // Lấy dữ liệu từ file dangky.php
    $username   = addslashes($_POST['txtUsername']);
    $password   = addslashes($_POST['txtPassword']);

    //Kiểm tra người dùng đã nhập liệu đầy đủ chưa
    if (!$username || !$password) {
        echo "Vui lòng nhập đầy đủ thông tin. <a href='javascript: history.go(-1)'>Trở lại</a>";
        exit;
    }

    //Kiểm tra tên đăng nhập này đã có người dùng chưa
    if(mysql_num_rows(mysql_query("SELECT user FROM user WHERE usern='$username'")) > 0){
        echo "Tên đăng nhập này đã có người dùng. Vui lòng chọn tên đăng nhập khác. <a href='javascript: history.go(-1)'>Trở lại</a>";
        exit;
    }

    // Lưu thông tin thành viên vào bảng
	@mysql_query("INSERT INTO user(`user`, `password`, `lock`) VALUES ('".$username."','".$password."',0);");
	@$query = mysql_query("SELECT user_id FROM user WHERE user='".$username."' LIMIT 1;");
	if(mysql_num_rows($query) == 0){
		echo "Lỗi thêm";
	} else {
		$row = mysql_fetch_array($query);
		$id  = $row['user_id'];
		@mysql_query("INSERT INTO armymem(id, xu, luong) VALUES (".$id.", 1000, 0);");
		// Thông báo quá trình lưu
		echo "Quá trình đăng ký thành công. ";
	}
?>
