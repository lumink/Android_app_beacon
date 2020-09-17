<?php

	if(isset($_POST["Token"])){

		$token = $_POST["Token"];
		$sessionid = $_POST["Sessionid"];		


		//데이터베이스에 접속해서 토큰을 저장
		include_once 'config.php';
		$conn = mysqli_connect("203.252.195.99","root","choojeok","joljak");
		$query = "INSERT INTO tokenusers(id, Token) Values ('$sessionid','$token') ON DUPLICATE KEY UPDATE Token = '$token'; ";
		mysqli_query($conn, $query);

		mysqli_close($conn);

	}
?>