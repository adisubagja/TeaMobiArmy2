<?php
    $conn['host'] = 'localhost'; // Tên server, nếu dùng hosting free thì cần thay đổi
    $conn['dbname'] = 'dbarmy2'; // Đây là tên của Database
    $conn['username'] = 'root'; // Tên sử dụng Database
    $conn['password'] = ''; // Mật khẩu của tên sử dụng Database
    @mysql_connect(
        "{$conn['host']}",
        "{$conn['username']}",
        "{$conn['password']}")
    or
        die("Không thể kết nối database");
    @mysql_select_db(
        "{$conn['dbname']}") 
    or
        die("Không thể chọn database");
?>