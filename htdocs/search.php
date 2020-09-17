<?php



$con=mysqli_connect("203.252.195.99","root","choojeok","joljak");



if (mysqli_connect_errno($con)){

	echo "Failed to connect to MySQL: " . mysqli_connect_error();

}
 



session_start();

$ID = $_GET['ID'];

$result = mysqli_query($con,"SELECT id, nickname FROM accountinfo where ID='$ID' ");
 

$row = mysqli_fetch_array($result);

$data = $row[0];

$data2 = $row[1];



if($data){

	echo 'ID  ' , $data;
	echo '      ';
	echo ' NICKNAME  ' , $data2;

}

else {

	echo 'No Such User Found';

}


mysqli_close($con);


?>


