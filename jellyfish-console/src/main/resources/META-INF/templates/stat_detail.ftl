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
<script type="text/javascript" src="${contextPath}/static/js/lib/map.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/app2.js"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com.cn/highcharts/highcharts-more.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://code.highcharts.com.cn/highcharts/modules/solid-gauge.js"></script>
<script src="https://img.hcharts.cn/highcharts-plugins/highcharts-zh_CN.js"></script>
<script src="https://code.highcharts.com/themes/dark-unica.js"></script>
</head>
<script>

	$(function(){
	
		loadSummaryChart();
		setInterval(function(){
			loadSummaryChart();
		},5000);
		
		loadStatisticChart('rt','rt','API Response Time Summary','Response Time (ms)',' ms');
		loadStatisticChart('cc', 'cc','API Concurrency Summary', 'Concurrency', '');
		loadStatisticChart('qps', 'qps','API QPS Summary', 'QPS', '');
		setInterval(function(){
			loadStatisticChart('rt','rt','API Response Time Summary','Response Time (ms)',' ms');
			loadStatisticChart('cc', 'cc','API Concurrency Summary', 'Concurrency', '');
			loadStatisticChart('qps', 'qps','API QPS Summary', 'QPS', '');
		},5000);
		
		loadCallCountChart();
		setInterval(function(){
			loadCallCountChart();
		},5000);
		
		loadHttpStatusCountChart();
		setInterval(function(){
			loadHttpStatusCountChart();
		},5000);
		
		loadCombinedChart();
		setInterval(function(){
			loadCombinedChart();
		},5000);
		
	});
	
	function loadSummaryChart(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"category": '${(catalog.category ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/catalog/summary',
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
						SummaryChartUtils.loadCallCountChart('countSummary','API Call Count Total', successCount, failedCount, timeoutCount);
						
						dataEntries = entries['httpStatus'];
						var countOf1xx=dataEntries['countOf1xx'];
						var countOf2xx=dataEntries['countOf2xx'];
						var countOf3xx=dataEntries['countOf3xx'];
						var countOf4xx=dataEntries['countOf4xx'];
						var countOf5xx=dataEntries['countOf5xx'];
						SummaryChartUtils.loadHttpStatusCountChart('httpStatusCountSummary','API Http Status Count Total', countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx);
						
						dataEntries = entries['rt'];
						values = [dataEntries['highestValue'], dataEntries['middleValue'], dataEntries['lowestValue']];
						SummaryChartUtils.loadStatisticChart('rtSummary', 'Response Time', 60000, values, 'ms');
						
						dataEntries = entries['qps'];
						values = [dataEntries['highestValue'], dataEntries['middleValue'], dataEntries['lowestValue']];
						SummaryChartUtils.loadStatisticChart('qpsSummary', 'QPS', 10000, values, '');
						
						dataEntries = entries['cc'];
						values = [dataEntries['highestValue'], dataEntries['middleValue'], dataEntries['lowestValue']];
						SummaryChartUtils.loadStatisticChart('ccSummary', 'Concurrency', 200, values, '');
					}
				}
		    });
	}
	
	function loadCombinedChart(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"category": '${(catalog.category ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/atlantis/jellyfish/catalog/combined/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: true,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var categories = [], count=[], rt=[], qps=[], cc=[];
						for(var category in entries){
							categories.push(category);
							count.push(entries[category]['count']);
							rt.push(entries[category]['rt-middleValue']);
							qps.push(entries[category]['qps-middleValue']);
							cc.push(entries[category]['cc-middleValue']);
						}
						SequenceChartUtils.loadCombinedChart('combined','API Response Time/QPS/Concurrency Summary', categories, count, rt, qps, cc);
					}
				}
		    });
	}
	
	function loadHttpStatusCountChart(){
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
						SequenceChartUtils.loadHttpStatusCountChart('httpStatus','API Http Status Count Summary', categories, countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx);
					}
				}
		    });
	}
	
	function loadCallCountChart(){
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
						SequenceChartUtils.loadCallCountChart('count','API Call Count Summary',categories, successCount, failedCount, timeoutCount);
					}
				}
		    });
	}
	
	function loadStatisticChart(metric, divId, title, yTitle, tip){
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
	}
	
</script>
<body>
	<#include "top.ftl">
	<div id="container">
		<div id="chartBox">
			<div id="infoBox">
				<div id="catalog" style="width: 20%; float: left;">
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
			</div>
			<div class="summaryBox">
				<div id="rtSummary" style="width: 35%; height:320px; float: right;"></div>
				<div id="qpsSummary" style="width: 35%; height:320px; float: right;"></div>
				<div id="ccSummary" style="width: 30%; height:320px; float: right;"></div>
			</div>
			<div class="summaryBox">
				<div id="countSummary" style="width: 50%; height:320px; float: right;"></div>
				<div id="httpStatusCountSummary" style="width: 50%; height:320px; float: right;"></div>
			</div>
			<div class="chartObj" id="rt"></div>
			<div class="chartObj" id="qps"></div>
			<div class="chartObj" id="cc"></div>
			<div class="chartObj" id="count"></div>
			<div class="chartObj" id="httpStatus"></div>
			<div class="chartObj" id="combined"></div>
			
		</div>
	</div>
	<#include "foot.ftl">
</body>
</html>