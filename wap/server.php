<?php
$url = "http://gmb.teamobi.com/srvip/army2list.txt";

// Kh?i t?o CURL
$ch = curl_init($url);
 
// Thi?t l?p có return
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$result = curl_exec($ch);
 
curl_close($ch);

echo "Local:127.0.0.1:8122,";
echo $result;
?>
