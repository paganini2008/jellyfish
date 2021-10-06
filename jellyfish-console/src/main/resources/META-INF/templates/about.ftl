<#setting number_format="#">
<#setting number_format="#">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>About Jellyfish</title>
<link rel="shortcut icon" href="#"/>
<script type="text/javascript">
	var $contextPath = '${contextPath}';
</script>
<link href="${contextPath}/static/css/base.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/static/js/lib/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/json2.js"></script>
<style type="text/css">
	
	.hidden{
		width: 100%;
		font-weight: bold;
	}
	    
</style>
<body>
	<#include "top.ftl">
	<div id="container">
		<iframe name="quickStartFrm" id="quickStart" src="${contextPath}/static/file/doc.html" frameborder="0" align="left" width="100%" height="100%" scrolling="yes">
    			<p>Your browser does not support iframe tag</p>
		</iframe>
	</div>
	<#include "foot.ftl">
</body>
</html>