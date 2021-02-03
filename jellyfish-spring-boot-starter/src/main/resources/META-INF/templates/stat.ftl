<#setting number_format="#">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Jellyfish History</title>
<link rel="shortcut icon" href="#"/>
<script type="text/javascript">
	var $contextPath = '${contextPath}';
</script>
<link href="${contextPath}/static/css/base.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/static/js/lib/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/json2.js"></script>
</head>
<script>

	$(function(){
		
		initialize();
	});
	
	function initialize(){
		$.ajax({
			    url: '${contextPath}/application/cluster/statistic/list',
				type:'get',
				contentType: 'application/json;charset=UTF-8',
				dataType:'json',
				success: function(data){
					var html = '';
					if(data.data != null) {
						$.each(data.data,function(i, item){
							html += '<tr>';
							html += '<td>' + item.clusterName + '</td>';
							html += '<td>' + item.applicationName + '</td>';
							html += '<td>' + item.host + '</td>';
							html += '<td>' + item.path + '</td>';
							html += '<td><a href="${contextPath}/application/cluster/statistic/detail?identifier=' + item.identifier + '" target="_blank" >View</a></td>';
							html += '</tr>';
						});
					}
					if(html.length > 0){
						$('#pathList tbody').html(html);
					}
				}
		});
	}
	
</script>
<body>
	<div id="top">
		<label id="title">Jellyfish (v2.0)</label>
	</div>
	<div id="container">
		<table id="pathList" width="100%" border="0" cellspacing="0" cellpadding="0" class="tblCom">
			<thead>
				<tr>
					<td>Cluster Name</td>
					<td>Application Name</td>
					<td>Host</td>
					<td>Path</td>
					<td>&nbsp;</td>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
	<div id="foot">
		Spring Dessert Series
	</div>
</body>
</html>