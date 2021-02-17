<#setting number_format="#">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Jellyfish Statistic</title>
<link rel="shortcut icon" href="#"/>
<script type="text/javascript">
	var $contextPath = '${contextPath}';
</script>
<link href="${contextPath}/static/css/base.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/static/js/lib/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/json2.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/map.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/app2.js"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://img.hcharts.cn/highcharts-plugins/highcharts-zh_CN.js"></script>
<script src="https://code.highcharts.com/themes/dark-unica.js"></script>
</head>
<script>

	$(function(){
		loadStatisticChart('rt','rt','API Response Time Summary','Response Time (ms)',' ms');
		loadStatisticChart('cc', 'cc','API Concurrency Summary', 'Concurrency', '');
		loadStatisticChart('qps', 'qps','API QPS Summary', 'QPS', '');
		loadCallChart();
		loadHttpStatusCountChart();
	});
	
	function loadHttpStatusCountChart(){
		setInterval(function(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"category": '${(catalog.category ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/catalog/httpStatus/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: true,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var categories = [], countOf1xx=[], countOf2xx=[], countOf3xx=[], countOf4xx=[], countOf5xx=[];
						for(var category in entries){
							categories.push(category);
							countOf1xx.push(entries[category]['countOf1xx']);
							countOf2xx.push(entries[category]['countOf2xx']);
							countOf3xx.push(entries[category]['countOf3xx']);
							countOf4xx.push(entries[category]['countOf4xx']);
							countOf5xx.push(entries[category]['countOf5xx']);
						}
						SequenceChartUtils.loadHttpStatusCountChart('httpStatus','Http Status Count Summary', categories, countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx);
					}
				}
		    });
		},5000);
	}
	
	function loadCallChart(){
		setInterval(function(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"category": '${(catalog.category ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/catalog/count/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: true,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var categories = [], successCount=[], failedCount=[], timeoutCount=[];
						for(var category in entries){
							categories.push(category);
							successCount.push(entries[category]['successCount']);
							failedCount.push(entries[category]['failedCount']);
							timeoutCount.push(entries[category]['timeoutCount']);
						}
						SequenceChartUtils.loadCallChart('count','API Call Summary',categories, successCount, failedCount, timeoutCount);
					}
				}
		    });
		},5000);
	}
	
	function loadStatisticChart(metric, divId, title, yTitle, tip){
		setInterval(function(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"category": '${(catalog.category ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/catalog/'+ metric + '/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: true,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var categories = [], highestValues=[], middleValues=[], lowestValues=[];
						for(var category in entries){
							categories.push(category);
							highestValues.push(entries[category]['highestValue']);
							middleValues.push(entries[category]['middleValue']);
							lowestValues.push(entries[category]['lowestValue']);
						}
						SequenceChartUtils.loadStatisticChart(divId, title, categories, highestValues, middleValues, lowestValues, yTitle, tip);
					}
				}
		    });
		},5000);
	}
	
</script>
<body>
	<div id="top">
		<label id="title">Jellyfish ${version!}</label>
	</div>
	<div id="container">
		<div id="chartBox">
			<div id="totalBox">
				<div id="catalog" style="width: 30%; float: left;">
					<p>
						<label>Cluster Name: </label>
						<span>${(catalog.clusterName)!}</span>
					</p>
					<p>
						<label>Application Name: </label>
						<span>${(catalog.applicationName)!}</span>
					</p>
					<p>
						<label>Host: </label>
						<span>${(catalog.host)!}</span>
					</p>
					<p>
						<label>Category: </label>
						<span>${(catalog.category)!}</span>
					</p>
					<p>
						<label>Path: </label>
						<span>${(catalog.path)!}</span>
					</p>
				</div>
				<div id="total" style="width: 60%; height:320px; float: right;"></div>
			</div>
			
			<div class="chartObj" id="rt"></div>
			<div class="chartObj" id="qps"></div>
			<div class="chartObj" id="cc"></div>
			<div class="chartObj" id="count"></div>
			<div class="chartObj" id="httpStatus"></div>
			<div class="chartObj" id="complex"></div>
			
		</div>
	</div>
	<div id="foot">
		Spring Dessert Series
	</div>
</body>
</html>