<!DOCTYPE html>
<html>
<head>
	<title>accounts for HYU students</title>
	<meta name ="author" Content="Web Application Project Team">
	<meta name ="keywords" Content="Lotte, mall, shopping, 롯데, 쇼핑">
	<meta name ="description" Content="GPS기반 쇼핑전용 애플리케이션입니다.">
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<!-- CSS -->
	    
    <link rel="stylesheet" href="./bootstrap.css" media="screen">
    <link rel="stylesheet" href="../assets/css/custom.min.css">
	<link rel="stylesheet" type="text/css" href="basic.css">
	<link rel="stylesheet" type="text/css" href="index.css">
	
	<!-- JS  -->
	<script src="prototype.js" type="text/javascript"></script>
	<script src="http://ajax.googleapis.com/ajax/libs/scriptaculous/1.9.0/scriptaculous.js" type="text/javascript"></script>
	<script src="index.js" type="text/javascript"></script>	

	
	<!--IS USER LOGINED NOW? CHECK IT -->
	<?php
	session_cache_expire(120);
	session_start();
	$_SESSION["S_ID"] = session_id();
	$sid = $_SESSION["S_ID"];

	try {

		$db = new PDO("mysql:dbname=moneybook","root","1111");
		$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		//CHECK USER IS LOGINED. IF SOMEONE HAS ALREADY LOGINED, DATA IS ON SESSION DB TABLE
		$rows = $db->query("SELECT * from session WHERE sid = '$sid'");
		$count = $rows->rowCount();
		//LOGINED USER. REDIRECT TO MAIN
		if($count > 0)
		{
			echo "<script> alert('hello!'); </script>";	
			header("Location: example.php");
			exit;
		}
		
		// IF USER REQUESTED LOGIN, AND ID PASSWOD IS WRONG, ALERT SOMETHING.
		if(isset($_SESSION['login']) && $_SESSION['login'] == FALSE)
		{

		}
		
	} catch (PDOException $ex) {
		header("HTTP/1.1 400 wrong request");
		Die("Sorry, Server status is not good. Error : $ex->getMessage()");
	}
	
	?>
</head>
<body>
	<div id="body_wrapper">
		<footer>
		</footer>
		<div id="a">
			<div id="loginwindow" class="window">
				<div>
					<h1> MEMBER LOGIN </h1>
				</div>
				<div id="inputwindow">
					<form action="login.php" method="post">
						<div>
						<label for="id"> ID </label>
						<input type="text" name="id" />
						</div>
						<div>
						<input id="submit" type="image" src="power43.png" alt="submit" value="Login" />
						</div>
						<div>
						<label for="pw"> PW </label>
						<input type="password" name="pw" />
						</div>
						<div>
						<input type="checkbox" name="remember" > Remember Me </input>
						<button type="button" id="signbtn"> Sign In </button>
						</div>			
					</form>
				</div>

			</div>
		</div>
		<div id="b">
			<div id="signwindow" class="window" style="display:none">
				<div>
					<h1> SIGN IN </h1>
				</div>
				<form>
					<div>
					<label for="uname"><span>name</span></label>
					<input id="username" type="text" name="uname" />
					</div>
					<div>
					<label for="id"><span>ID</span></label>
					<input id="userid" type="text" name="id" />
					</div>
					<div>
					<label for="pw"><span>PW</span></label>
					<input id="userpw" type="password" name="pw" />
					</div>
					<div>
					<label for="pwconfirm"><span>PW again</span></label>
					<input id="pwconfirm" type="password" name="pwconfirm" />
					</div>
					<div>
					<button type="button" id="signinBtn"> Sign In </button>		
					<button type="button" id="cancelBtn"> Cancel </button>		
				</form>
			</div>
		</div>

		
	</div>	
</body>
</html>
