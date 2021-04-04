<#setting number_format="#">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Jellyfish Http Watcher</title>
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
		$('#nav li').hover(function(){
			$(this).css({'text-decoration':'underline'});
		},function(){
			$(this).css({'text-decoration':'none'});
		});
	
		$('#groupBox ul li').click(function(){
			$(this).siblings().css({'font-weight':'normal','background-color':''});
			var index = $('#groupBox ul li').index($(this));
			$('#groupBox ul li').slice(0, index + 1).each(function(){
				$(this).css({'font-weight':'bold','background-color':'red'});
			});
			renderTable(index);
		});
		
		renderTable(0);
		
	});
	
	function renderTable(level){
		$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/api/list?level=' + level,
				type:'get',
				contentType: 'application/json;charset=UTF-8',
				dataType:'json',
				success: function(data){
					var html = '';
					if(data.data != null) {
						$.each(data.data,function(i, item){
							html += '<tr>';
							html += '<td class="tdRight10" width="5%">' + (i + 1) + '</td>';
							html += '<td class="tdLeft10" width="10%">' + item.clusterName + '</td>';
							html += '<td class="tdLeft10" width="10%">' + item.applicationName + '</td>';
							html += '<td class="tdLeft10" width="10%">' + item.host + '</td>';
							html += '<td class="tdLeft10" width="10%">' + item.category + '</td>';
							html += '<td class="tdLeft10">' + item.path + '</td>';
							html += '<td class="tdRight10" width="10%"><a href="${contextPath}/atlantis/jellyfish/http/detail?identifier=' + item.identifier + '" target="_blank" >View</a></td>';
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
	<#include "top.ftl">
	<div id="container">
		<div id="groupBox">
			<label>Group By: </label>
			<ul>
				<li style="font-weight: bold; background-color: red;">Cluster Name</li>
				<li>Application Name</li>
				<li>Host</li>
				<li>Category</li>
				<li>Path</li>
			</ul>
		</div>
		<div id="tableBox">
			<table id="pathList" width="100%" border="0" cellspacing="0" cellpadding="0" class="tblCom">
				<thead>
					<tr>
						<td class="tdRight10" width="5%">No.</td>
						<td class="tdLeft10" width="10%">Cluster Name</td>
						<td class="tdLeft10" width="10%">Application Name</td>
						<td class="tdLeft10" width="10%">Host</td>
						<td class="tdLeft10" width="10%">Category</td>
						<td class="tdLeft10">Path</td>
						<td class="tdRight10" width="10%">&nbsp;</td>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
	</div>
	<#include "foot.ftl">
</body>
</html>