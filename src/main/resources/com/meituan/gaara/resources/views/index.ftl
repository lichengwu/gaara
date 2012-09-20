<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Gaara Monitoring</title>
		<link type="text/css" href="?type=resource&key=static/css/start/jquery-ui-1.8.18.custom.css" rel="stylesheet" />	
		<script type="text/javascript" src="?type=resource&key=static/js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="?type=resource&key=static/js/jquery-ui-1.8.18.custom.min.js"></script>
	</head>
	<body>
		<h2>已注册的应用</h2>
		<ul>
			<#list apps?keys as app>
				<li>${app}</li>
			</#list>
		</ul>
	</body>
</html>