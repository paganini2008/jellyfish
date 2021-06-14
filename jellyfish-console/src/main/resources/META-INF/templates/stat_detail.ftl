<#setting number_format="#">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Jellyfish Http Monitor</title>
<link rel="shortcut icon" href="#"/>
<script type="text/javascript">
	var $contextPath = '${contextPath}';
</script>
<link href="${contextPath}/static/css/base.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/static/js/lib/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/json2.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/map.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/app2.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/highcharts.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/highcharts-more.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/modules/exporting.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/modules/solid-gauge.js"></script>
<script src="https://img.hcharts.cn/highcharts-plugins/highcharts-zh_CN.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/themes/dark-unica.js"></script>
</head>
<script>

	$(function(){
	
		loadApiSummaryChart();
		setInterval(function(){
			loadApiSummaryChart();
		},5000);
		
		loadApiLatestChart();
		setInterval(function(){
			loadApiLatestChart();
		},5000);
		
		loadApiStatisticChart('rt','rt','API Response Time Summary','Response Time (ms)',' ms');
		loadApiStatisticChart('cc', 'cc','API Concurrency Summary', 'Concurrency', '');
		loadApiStatisticChart('qps', 'qps','API QPS Summary', 'QPS', '');
		setInterval(function(){
			loadApiStatisticChart('rt','rt','API Response Time Summary','Response Time (ms)',' ms');
			loadApiStatisticChart('cc', 'cc','API Concurrency Summary', 'Concurrency', '');
			loadApiStatisticChart('qps', 'qps','API QPS Summary', 'QPS', '');
		},5000);
		
		loadApiCountChart();
		setInterval(function(){
			loadApiCountChart();
		},5000);
		
		loadHttpStatusCountChart();
		setInterval(function(){
			loadHttpStatusCountChart();
		},5000);
		
		renderSummary();
		setInterval(function(){
			renderSummary();
		},5000);
	});
	
	function loadApiLatestChart(){
			var param = {
					"clusterName": '${(api.clusterName)!}',
					"applicationName": '${(api.applicationName)!}',
					"host": '${(api.host ? html)!}',
					"category": '${(api.category ? html)!}',
					"path": '${(api.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/api/latest/sequence',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: true,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						dataEntries = entries['rt'];
						values = [dataEntries['highestValue']];
						SummaryChartUtils.loadApiStatisticChart('rtSummary', 'Response Time', 60000, values, ' ms');
						
						dataEntries = entries['qps'];
						values = [dataEntries['highestValue']];
						SummaryChartUtils.loadApiStatisticChart('qpsSummary', 'QPS', 10000, values, ' Per second');
						
						dataEntries = entries['cc'];
						values = [dataEntries['highestValue']];
						SummaryChartUtils.loadApiStatisticChart('ccSummary', 'Concurrency', 200, values, '');
					}
				}
		    });
	}
	
	function loadApiSummaryChart(){
			var param = {
					"clusterName": '${(api.clusterName)!}',
					"applicationName": '${(api.applicationName)!}',
					"host": '${(api.host ? html)!}',
					"category": '${(api.category ? html)!}',
					"path": '${(api.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/api/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: true,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var dataEntries = entries['count'];
						var successCount=dataEntries['successCount'];
						var failedCount=dataEntries['failedCount'];
						var timeoutCount=dataEntries['timeoutCount'];
						SummaryChartUtils.loadApiCountChart('apiCountSummary','API Count Summary', successCount, failedCount, timeoutCount);
						
						dataEntries = entries['httpStatus'];
						var countOf1xx=dataEntries['countOf1xx'];
						var countOf2xx=dataEntries['countOf2xx'];
						var countOf3xx=dataEntries['countOf3xx'];
						var countOf4xx=dataEntries['countOf4xx'];
						var countOf5xx=dataEntries['countOf5xx'];
						SummaryChartUtils.loadHttpStatusCountChart('httpStatusCountSummary','API Http Status Summary', countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx);
					
					}
				}
		    });
	}
	
	function loadHttpStatusCountChart(){
			var param = {
					"clusterName": '${(api.clusterName)!}',
					"applicationName": '${(api.applicationName)!}',
					"host": '${(api.host ? html)!}',
					"category": '${(api.category ? html)!}',
					"path": '${(api.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/api/httpStatus/sequence',
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
							countOf1xx.push(entries[category]['httpStatus']['countOf1xx']);
							countOf2xx.push(entries[category]['httpStatus']['countOf2xx']);
							countOf3xx.push(entries[category]['httpStatus']['countOf3xx']);
							countOf4xx.push(entries[category]['httpStatus']['countOf4xx']);
							countOf5xx.push(entries[category]['httpStatus']['countOf5xx']);
						}
						SequenceChartUtils.loadHttpStatusCountChart('httpStatus','API Http Status Count Summary', categories, countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx);
					}
				}
		    });
	}
	
	function loadApiCountChart(){
			var param = {
					"clusterName": '${(api.clusterName)!}',
					"applicationName": '${(api.applicationName)!}',
					"host": '${(api.host ? html)!}',
					"category": '${(api.category ? html)!}',
					"path": '${(api.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/api/count/sequence',
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
							successCount.push(entries[category]['count']['successCount']);
							failedCount.push(entries[category]['count']['failedCount']);
							timeoutCount.push(entries[category]['count']['timeoutCount']);
						}
						SequenceChartUtils.loadApiCountChart('count','API Count Summary',categories, successCount, failedCount, timeoutCount);
					}
				}
		    });
	}
	
	function loadApiStatisticChart(metric, divId, title, yTitle, tip){
			var param = {
					"clusterName": '${(api.clusterName)!}',
					"applicationName": '${(api.applicationName)!}',
					"host": '${(api.host ? html)!}',
					"category": '${(api.category ? html)!}',
					"path": '${(api.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/api/'+ metric + '/sequence',
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
							highestValues.push(entries[category][metric]['highestValue']);
							middleValues.push(entries[category][metric]['middleValue']);
							lowestValues.push(entries[category][metric]['lowestValue']);
						}
						SequenceChartUtils.loadApiStatisticChart(divId, title, categories, highestValues, middleValues, lowestValues, yTitle, tip);
					}
				}
		    });
	}
	
	function renderSummary(){
		var param = {
					"clusterName": '${(api.clusterName)!}',
					"applicationName": '${(api.applicationName)!}',
					"host": '${(api.host ? html)!}',
					"category": '${(api.category ? html)!}',
					"path": '${(api.path ? html)!}'
				};
		$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/api/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: true,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var html = '';
						var dataEntries = entries['rt'];
						var highestValue = dataEntries['highestValue'];
						var middleValue = dataEntries['middleValue'];
						var lowestValue = dataEntries['lowestValue'];
						html += '<p>';
						html += '<label>Response Time: </label>';
						html += '<span>' + highestValue + '/' + middleValue + '/' + lowestValue + '</span>';
						html += '</p>';
						dataEntries = entries['qps'];
						highestValue = dataEntries['highestValue'];
						middleValue = dataEntries['middleValue'];
						lowestValue = dataEntries['lowestValue'];
						html += '<p>';
						html += '<label>QPS: </label>';
						html += '<span>' + highestValue + '/' + middleValue + '/' + lowestValue + '</span>';
						html += '</p>';
						dataEntries = entries['cc'];
						highestValue = dataEntries['highestValue'];
						middleValue = dataEntries['middleValue'];
						lowestValue = dataEntries['lowestValue'];
						html += '<p>';
						html += '<label>Concurrency: </label>';
						html += '<span>' + highestValue + '/' + middleValue + '/' + lowestValue + '</span>';
						html += '</p>';
						$('.summary:eq(1)').html(html);
						html = '';
						
						dataEntries = entries['count'];
						var successCount=dataEntries['successCount'];
						var failedCount=dataEntries['failedCount'];
						var timeoutCount=dataEntries['timeoutCount'];
						html += '<p>';
						html += '<label>Success Count: </label>';
						html += '<span>' + successCount + '</span>';
						html += '</p>';
						html += '<p>';
						html += '<label>Failure Count: </label>';
						html += '<span>' + failedCount + '</span>';
						html += '</p>';
						html += '<p>';
						html += '<label>Timeout Count: </label>';
						html += '<span>' + timeoutCount + '</span>';
						html += '</p>';
						$('.summary:eq(2)').html(html);
						html = '';
						
						dataEntries = entries['httpStatus'];
						var countOf1xx=dataEntries['countOf1xx'];
						var countOf2xx=dataEntries['countOf2xx'];
						var countOf3xx=dataEntries['countOf3xx'];
						var countOf4xx=dataEntries['countOf4xx'];
						var countOf5xx=dataEntries['countOf5xx'];
						html += '<p>';
						html += '<label>Count of 1xx: </label>';
						html += '<span>' + countOf1xx + '</span>';
						html += '</p>';
						html += '<p>';
						html += '<label>2xx: </label>';
						html += '<span>' + countOf2xx + '</span>';
						html += '</p>';
						html += '<p>';
						html += '<label>3xx: </label>';
						html += '<span>' + countOf3xx + '</span>';
						html += '</p>';
						html += '<p>';
						html += '<label>4xx: </label>';
						html += '<span>' + countOf4xx + '</span>';
						html += '</p>';
						html += '<p>';
						html += '<label>5xx: </label>';
						html += '<span>' + countOf5xx + '</span>';
						html += '</p>';
						$('.summary:eq(3)').html(html);
						html = '';
					}
				}
		    });
	}
	
</script>
<body>
	<#include "top.ftl">
	<div id="container">
		<div id="chartBox">
			<div style="height: 30px; "></div>
			<div id="infoBox">
				<div class="summary" id="catalog">
					<p>
						<label>Cluster Name: </label>
						<span>${(api.clusterName)!}</span>
					</p>
					<p>
						<label>Application Name: </label>
						<span>${(api.applicationName)!}</span>
					</p>
					<p>
						<label>Host: </label>
						<span>${(api.host)!}</span>
					</p>
					<p>
						<label>Category: </label>
						<span>${(api.category)!}</span>
					</p>
					<p>
						<label>Path: </label>
						<span>${(api.path)!}</span>
					</p>
				</div>
				<div class="summary">
				</div>
				<div class="summary">
				</div>
				<div class="summary">
				</div>

			</div>
			<div class="summaryBox">
				<div id="rtSummary" style="width: 35%; height:320px; float: right;"></div>
				<div id="qpsSummary" style="width: 35%; height:320px; float: right;"></div>
				<div id="ccSummary" style="width: 30%; height:320px; float: right;"></div>
			</div>
			<div class="summaryBox">
				<div id="apiCountSummary" style="width: 50%; height:320px; float: right;"></div>
				<div id="httpStatusCountSummary" style="width: 50%; height:320px; float: right;"></div>
			</div>
			<div class="chartObj" id="rt"></div>
			<div class="chartObj" id="qps"></div>
			<div class="chartObj" id="cc"></div>
			<div class="chartObj" id="count"></div>
			<div class="chartObj" id="httpStatus"></div>
		</div>
	</div>
	<#include "foot.ftl">
</body>
</html>