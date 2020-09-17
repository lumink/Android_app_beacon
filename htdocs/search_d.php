<?php



$con=mysqli_connect("203.252.195.99","root","choojeok","joljak");



if (mysqli_connect_errno($con)){

	echo "Failed to connect to MySQL: " . mysqli_connect_error();

}
 



session_start();

$ID = $_GET['ID'];

$result = mysqli_query($con,"SELECT friend FROM friendlist where friend='$ID' ");
 

$row = mysqli_fetch_array($result);

$data = $row[0];



if($data){

	echo 'ID  ' , $data;
}

else {

	echo 'no such friend exist';

}


mysqli_close($con);


?>


