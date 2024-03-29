<#setting number_format="#">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Application Logging</title>
<link rel="shortcut icon" href="#"/>
<script type="text/javascript">
	var $contextPath = '${contextPath}';
</script>
<link href="${contextPath}/static/css/base.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/static/js/lib/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/json2.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/app.js"></script>
</head>
<script>
	var latestTop;
	
	$(function(){
		setInterval(doSearch, 1000);
		
		latestTop = $('#logBox').scrollTop();
		
		$('#logBox').scroll(function(){
			var top = $('#logBox').scrollTop();
			if(latestTop != top){
				if(latestTop > top){
					scrollState = 'up';
				}else{
					scrollState = 'down';
				}
				latestTop = top;
			}
		});
	});
</script>
<body>
	<#include "top.ftl">
	<div id="container">
		<div id="searchBox">
			<form id="searchFrm" action="${contextPath}/atlantis/jellyfish/log/entry/search" method="post">
				<fieldset>
				<legend>Query Condition</legend>
				<div class="searchCondition">
					<span>
						<label>Cluster Name: </label>
						<input type="text" value="default" name="clusterName"/>
					</span>
					<span>
						<label>Application Name: </label>
						<input type="text" value="" name="applicationName"/>
					</span>
					<span>
						<label>Host: </label>
						<input type="text" value="" name="host"/>
					</span>
					<span>
						<label>Identifier: </label>
						<input type="text" value="" name="identifier"/>
					</span>
				</div>
				<div class="searchCondition">
					<span style="width: 50%;">
						<label>Logger Name: </label>
						<input type="text" value="" name="loggerName"/>
					</span>
					<span>
						<label>Level: </label>
						<input type="text" value="" name="level"/>
					</span>
					<span>
						<label>Marker: </label>
						<input type="text" value="" name="marker"/>
					</span>
				</div>
				<div class="searchCondition">
					<span style="width: 75%">
						<label>Keyword: </label>
						<input type="text" value="" name="keyword" id="keyword"/>
					</span>
					<span style="width: 25%">
						<label>&nbsp;</label>
						<b>Asc</b><input type="radio" value="true" name="asc" checked="true"/>
						<b>Desc</b><input type="radio" value="false" name="asc"/>
					</span>
				</div>
				<div class="searchCondition">
					<span style="width: 75%">
					</span>
					<span style="width: 25%">
						<input type="button" id="searchBtn" value="Search"/>
					</span>
				</div>
				</fieldset>
			</form>
		</div>
		<div id="logBox">
		</div>
	</div>
	<#include "foot.ftl">
</body>
</html>