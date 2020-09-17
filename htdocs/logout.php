<?php
 session_start();

$hostname_localhost ="203.252.195.99";
    $database_localhost ="joljak";
    $username_localhost ="root";
    $password_localhost ="choojeok";
    $localhost = mysql_connect($hostname_localhost,$username_localhost,$password_localhost)
    or
    trigger_error(mysql_error(),E_USER_ERROR);
  
    mysql_select_db($database_localhost, $localhost);

session_destroy();
     
?>