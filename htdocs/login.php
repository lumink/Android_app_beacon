<?php
 
    $hostname_localhost ="203.252.195.99";
    $database_localhost ="joljak";
    $username_localhost ="root";
    $password_localhost ="choojeok";
    $localhost = mysql_connect($hostname_localhost,$username_localhost,$password_localhost)
    or
    trigger_error(mysql_error(),E_USER_ERROR);
    mysql_select_db($database_localhost, $localhost);


session_start();
  
    $username = $_POST['id'];
    $password = $_POST['password'];

    $query_search = "select * from accountinfo where id = '".$username."' AND pw = '".$password. "'";
    $query_exec = mysql_query($query_search) or die(mysql_error());
    $rows = mysql_num_rows($query_exec);
     
if($rows == 0){ 
	echo "No Such User Found"; 
	}
else{
	echo "User Found";   
}

	$userid=$row[id];
	$usernickname=$row[nickname];
	
	$_SESSION['userid']=$userid;
	$_SESSION['usernickname']=$usernickname;
     
?>