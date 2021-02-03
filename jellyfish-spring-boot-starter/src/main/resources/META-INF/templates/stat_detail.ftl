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
<script type="text/javascript" src="${contextPath}/static/js/lib/map.js"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://img.hcharts.cn/highcharts-plugins/highcharts-zh_CN.js"></script>
<script src="https://code.highcharts.com/themes/dark-unica.js"></script>
</head>
<script>
	var map = new Map();

	$(function(){
		loadTotalChart();
		loadRtChart();
		loadRealtimeChart('cons', 'consChart','Realtime Concurrency Summary', 'Concurrency', '');
		loadRealtimeChart('qps', 'qpsChart','Realtime QPS Summary', 'qps', '');
	});
	
	function loadTotalChart(){
		setInterval(function(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/application/cluster/statistic/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var data = [{
										name: 'Success Execution Count',
										y: entries['successExecutionCount']
									}, {
										name: 'Failed Execution Count',
										y: entries['failedExecutionCount']
									}, {
										name: 'Timeout Execution Count',
										y: entries['timeoutExecutionCount']
									}]
						doLoadPieChart('total','Total Summary', data);
					}
				}
		    });
		},5000);
	}
	
	function loadRtChart(){
		setInterval(function(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/application/cluster/statistic/rt/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: false,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var categories = [], highestValues=[], middleValues=[];
						var failedCount = [], timeoutCount = [], successCount = [];
						for(var category in entries){
							categories.push(category);
							highestValues.push(entries[category]['highestValue']);
							middleValues.push(entries[category]['middleValue']);
							failedCount.push(entries[category]['failedCount']);
							timeoutCount.push(entries[category]['timeoutCount']);
							successCount.push(entries[category]['successCount']);
						}
						
						doLoadRealtimeChart('rt','Realtime Response Time Summary',categories,highestValues, middleValues, 'Response Time (ms)', ' ms');
						doLoadAreaChart('count', 'Realtime Count Summary', categories, successCount, failedCount, timeoutCount);
					}
				}
		    });
		},5000);
	}
	
	function loadRealtimeChart(metric, divId, title, yTitle, tooltipUnit){
		setInterval(function(){
			var param = {
					"clusterName": '${(catalog.clusterName)!}',
					"applicationName": '${(catalog.applicationName)!}',
					"host": '${(catalog.host ? html)!}',
					"path": '${(catalog.path ? html)!}'
				};
			$.ajax({
			    url: '${contextPath}/application/cluster/statistic/' + metric +'/summary',
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(param),
				dataType:'json',
				async: false,
				success: function(data) {
					var entries = data.data;
					if(entries != null){
						var categories = [], highestValues=[], middleValues=[];
						for(var category in entries){
							categories.push(category);
							highestValues.push(entries[category]['highestValue']);
							middleValues.push(entries[category]['middleValue']);
						}
						doLoadRealtimeChart(divId,title,categories,highestValues, middleValues, yTitle, tooltipUnit);
					}
				}
		    });
		},5000);
	}
	
	function doLoadPieChart(divId, title, data){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			chart.update({
				series: [{
						name: 'Brands',
						colorByPoint: true,
						data: data
					}]
			});
		}else{
			var chart = Highcharts.chart(divId, {
				chart: {
						plotBackgroundColor: null,
						plotBorderWidth: null,
						plotShadow: false,
						type: 'pie'
				},
				title: {
						text: title
				},
				tooltip: {
						pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
				},
				plotOptions: {
						pie: {
								allowPointSelect: true,
								cursor: 'pointer',
								dataLabels: {
										enabled: false
								},
								showInLegend: true
						}
				},
				series: [{
						name: 'Brands',
						colorByPoint: true,
						data: data
					}]
			});
			map.put(divId, chart);
		}
		
	}
	
	function doLoadAreaChart(divId, title, categories, success, failed, timeout){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			var data = [{
			        name: 'Success Count',
			        data: success
			    }, {
			        name: 'Failed Count',
			        data: failed
			    }, {
			        name: 'Timeout Count',
			        data: timeout
			    }];
			chart.update({
				xAxis: {
			        categories: categories,
			        tickmarkPlacement: 'on',
			        title: {
			            enabled: false
			        }
			    },
				series: data
			});
		}else{
			var configData = {
		    chart: {
		        type: 'area'
		    },
		    title: {
		        text: title
		    },
		    xAxis: {
		        categories: categories,
		        tickmarkPlacement: 'on',
		        title: {
		            enabled: false
		        }
		    },
		    yAxis: {
		        title: {
		            text: 'Percent'
		        }
		    },
		    tooltip: {
		        pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.1f}%</b> ({point.y:,.0f})<br/>',
		        shared: true
		    },
		    plotOptions: {
		        area: {
		            stacking: 'percent',
		            lineColor: '#ffffff',
		            lineWidth: 1,
		            marker: {
		                lineWidth: 1,
		                lineColor: '#ffffff'
		            }
		        }
		    },
		    series: [{
		        name: 'Success Count',
		        data: success
			    }, {
			        name: 'Failed Count',
			        data: failed
			    }, {
			        name: 'Timeout Count',
			        data: timeout
			    }]
			};
			var chart = Highcharts.chart(divId, configData);
			map.put(divId, chart);
		}
	}
	
	function doLoadRealtimeChart(divId,title,categories,highestValues, middleValues,yTitle,tooltipUnit){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			var data = [{
			        name: 'Maximum',
			        data: highestValues
			    }, {
			        name: 'Average',
			        data: middleValues
			    }];
			chart.update({
				xAxis: {
			        categories: categories
			    },
				series: data
			});
		}else{
			var chart = Highcharts.chart(divId, {
			    chart: {
			        type: 'areaspline'
			    },
			    title: {
			        text: title
			    },
			    legend: {
			        layout: 'vertical',
			        align: 'left',
			        verticalAlign: 'top',
			        x: 150,
			        y: 100,
			        floating: true,
			        borderWidth: 1,
			        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
			    },
			    xAxis: {
			        categories: categories
			    },
			    yAxis: {
			        title: {
			            text: yTitle
			        }
			    },
			    tooltip: {
			        shared: true,
			        valueSuffix: tooltipUnit
			    },
			    plotOptions: {
			        areaspline: {
			            fillOpacity: 0.5
			        }
			    },
			    series: [{
			        name: 'Maximum',
			        data: highestValues
			    }, {
			        name: 'Average',
			        data: middleValues
			    }]
			});
			map.put(divId, chart);
		}
	}
	
	
</script>
<body>
	<div id="top">
		<label id="title">Jellyfish (v2.0)</label>
	</div>
	<div id="container">
		<div id="chartBox">
			<div id="totalBox">
				<div id="catalog" style="width: 30%; float: left;"></div>
				<div id="total" style="width: 60%; height:360px; float: right;"></div>
			</div>
			<div id="rt" style="width: 100%; height:360px;"></div>
			<div id="count" style="width: 100%; height:360px;"></div>
			<div id="consChart" style="width: 100%; height:360px;"></div>
			<div id="qpsChart" style="width: 100%; height:360px;"></div>
		</div>
	</div>
	<div id="foot">
		Spring Dessert Series
	</div>
</body>
</html>