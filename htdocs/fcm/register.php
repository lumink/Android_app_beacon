<?php

	if(isset($_POST["Token"])){

		$token = $_POST["Token"];
		$sessionid = $_POST["Sessionid"];		


		//�����ͺ��̽��� �����ؼ� ��ū�� ����
		include_once 'config.php';
		$conn = mysqli_connect("203.252.195.99","root","choojeok","joljak");
		$query = "INSERT INTO tokenusers(id, Token) Values ('$sessionid','$token') ON DUPLICATE KEY UPDATE Token = '$token'; ";
		mysqli_query($conn, $query);

		mysqli_close($conn);

	}
?>