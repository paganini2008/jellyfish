var map = new Map();

$(function(){
	$('#nav li').hover(function(){
		$(this).css({'text-decoration':'underline'});
	},function(){
		$(this).css({'text-decoration':'none'});
	})
});

var SummaryChartUtils = {
		
	loadApiStatisticChart: function(divId, title, max, values, unit){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			var point = chart.series[0].points[0];
			point.update(values[0]);
		}else{
			var chart = Highcharts.chart(divId, {
				chart: {
			        type: 'solidgauge'
			    },
			    title: null,
			    pane: {
			        center: ['50%', '85%'],
			        size: '120%',
			        startAngle: -90,
			        endAngle: 90,
			        background: {
			            backgroundColor: Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
			            innerRadius: '60%',
			            outerRadius: '100%',
			            shape: 'arc'
			        }
			    },
			    exporting: {
			        enabled: false
			    },
			    tooltip: {
			        enabled: true
			    },
			    yAxis: {
			        stops: [
			            [0.1, '#55BF3B'], // green
			            [0.5, '#DDDF0D'], // yellow
			            [0.9, '#DF5353'] // red
			        ],
			        lineWidth: 0,
			        tickWidth: 0,
			        minorTickInterval: null,

			        title: {
			            y: -70,
			            text: title
			        },
			        labels: {
			            y: 16
			        },
			        min: 0,
			        max: max
			    },
			    credits: {
			        enabled: false
			    },
			    plotOptions: {
			        solidgauge: {
			            dataLabels: {
			                y: 5,
			                borderWidth: 0,
			                useHTML: true
			            }
			        }
			    },
			    series: [{
			        name: title,
			        data: values,
			        dataLabels: {
			            format: '<div style="text-align:center"><span style="font-size:25px;">{y}</span><br/>' +
			            '<span style="font-size:12px;opacity:0.4;">' + unit + '</span></div>'
			        },
			        tooltip: {
			            valueSuffix: unit
			        }
			    }]
			});
			map.put(divId, chart);
		}
		
	},
		
	loadHttpStatusCountChart: function(divId, title, countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			chart.update({
				series: [{
					name: 'Calls/Percentage',
					colorByPoint: true,
					data: [
						{
							name: '1xx',
					    	y: countOf1xx
						},
						{
							name: '2xx',
					    	y: countOf2xx
						},
						{
							name: '3xx',
					    	y: countOf3xx
						},
						{
							name: '4xx',
					    	y: countOf4xx
						},
						{
							name: '5xx',
					    	y: countOf5xx
						}
					]
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
					pointFormat: '{series.name}: <b>&nbsp;{point.y}/{point.percentage:.1f} %</b>'
				},
			    exporting: {
			        enabled: false
			    },
				plotOptions: {
					pie: {
							allowPointSelect: true,
							cursor: 'pointer',
							dataLabels: {
									enabled: true,
									format: '<b>{point.name}</b>:&nbsp;{point.y}/{point.percentage:.1f} %',
							}
					}
				},
				series: [{
						name: 'Calls/Percentage',
						colorByPoint: true,
						data: [
							{
								name: '1xx',
						    	y: countOf1xx
							},
							{
								name: '2xx',
						    	y: countOf2xx
							},
							{
								name: '3xx',
						    	y: countOf3xx
							},
							{
								name: '4xx',
						    	y: countOf4xx
							},
							{
								name: '5xx',
						    	y: countOf5xx
							}
						]
				}]
			});
			map.put(divId, chart);
		}
	},
		
	loadApiCountChart: function(divId, title, successCount, failedCount, timeoutCount){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			chart.update({
				series: [{
					name: 'Count/Percentage',
					colorByPoint: true,
					data: [
						{
							name: 'Success Calls',
					    	y: successCount
						},
						{
							name: 'Failed Calls',
					    	y: failedCount
						},
						{
							name: 'Timeout Calls',
					    	y: timeoutCount
						}
					]
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
					pointFormat: '{series.name}: <b>&nbsp;{point.y}/{point.percentage:.1f} %</b>'
				},
			    exporting: {
			        enabled: false
			    },
				accessibility: {
			        point: {
			            valueSuffix: '%'
			        }
			    },
			    plotOptions: {
					pie: {
							allowPointSelect: true,
							cursor: 'pointer',
							dataLabels: {
									enabled: true,
									format: '<b>{point.name}</b>:&nbsp;{point.y}/{point.percentage:.1f} %',
							}
					}
			    },
				series: [{
					name: 'Calls/Percentage',
					colorByPoint: true,
					data: [
						{
							name: 'Succes Count',
					    	y: successCount
						},
						{
							name: 'Failed Count',
					    	y: failedCount
						},
						{
							name: 'Timeout Count',
					    	y: timeoutCount
						}
					]
				}]
			});
			map.put(divId, chart);
		}
	}
}

var SequenceChartUtils = {

	loadApiCountChart: function(divId, title, categories, successCount, failedCount, timeoutCount){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			var data = [{
			        name: 'Success Count',
			        data: successCount
			    }, {
			        name: 'Failed Count',
			        data: failedCount
			    }, {
			        name: 'Timeout Count',
			        data: timeoutCount
			    }];
			chart.update({
				xAxis: {
			        categories: categories,
			        allowDecimals: false
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
			        allowDecimals: false
			    },
			    yAxis: {
			        title: {
			            text: 'Http Request Calls'
			        }
			    },
			    tooltip: {
			    	pointFormat: '{series.name}: <b>{point.y:,.0f}</b>'
			    },
			    exporting: {
			        enabled: false
			    },
			    series: [{
				        name: 'Success Count',
				        data: successCount
				    }, {
				        name: 'Failed Count',
				        data: failedCount
				    }, {
				        name: 'Timeout Count',
				        data: timeoutCount
				    }]
			};
			var chart = Highcharts.chart(divId, configData);
			map.put(divId, chart);
		}
	},
	
	loadHttpStatusCountChart: function(divId, title, categories, countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			var data = [{
		        name: '1xx',
		        data: countOf1xx
		    }, {
		        name: '2xx',
		        data: countOf2xx
		    }, {
		        name: '3xx',
		        data: countOf3xx
		    }, {
		        name: '4xx',
		        data: countOf4xx
		    }, {
		        name: '5xx',
		        data: countOf5xx
		    }];
			chart.update({
				xAxis: {
			        categories: categories,
			        allowDecimals: false
			    },
				series: data
			});
		}else{
			var chart = Highcharts.chart(divId, {
				    chart: {
				        type: 'area'
				    },
				    title: {
				        text: title
				    },
				    xAxis: {
				        categories: categories,
				        allowDecimals: false
					},
				    yAxis: {
				        title: {
				            text: 'Http Request Calls'
				        }
				    },
				    tooltip: {
				    	pointFormat: '{series.name}: <b>{point.y:,.0f}</b>'
				    },
				    exporting: {
				        enabled: false
				    },
				    series: [{
				        name: '1xx',
				        data: countOf1xx
				    }, {
				        name: '2xx',
				        data: countOf2xx
				    }, {
				        name: '3xx',
				        data: countOf3xx
				    }, {
				        name: '4xx',
				        data: countOf4xx
				    }, {
				        name: '5xx',
				        data: countOf5xx
				    }]
				});
			map.put(divId, chart);
		}
	},
	
	loadCombinedChart: function(divId, title, categories, call, rt, qps, cc){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			var data = [{
						name: 'Calls',
						type: 'column',
						yAxis: 1,
						data: call,
						tooltip: {
							valueSuffix: ''
						}
					}, {
							name: 'Response Time',
							type: 'spline',
							yAxis: 2,
							data: rt,
							tooltip: {
								valueSuffix: ' ms'
							}
					}, {
						name: 'Concurrency',
						type: 'spline',
						data: cc,
						dashStyle: 'Dash',
						tooltip: {
							valueSuffix: ''
						}
					}, {
							name: 'QPS',
							type: 'spline',
							yAxis: 3,
							data: qps,
							dashStyle: 'LongDash',
							tooltip: {
								valueSuffix: ''
							}
					}];
			chart.update({
				xAxis: [{
					categories: categories,
					crosshair: true
				}],
				series: data
			});
		}else{
			var chart = Highcharts.chart(divId, {
				chart: {
						zoomType: 'xy'
				},
				title: {
						text: title
				},
				xAxis: [{
						categories: categories,
						crosshair: true
				}],
				yAxis: [{
						labels: {
								format: '{value} ms',
								style: {
										color: Highcharts.getOptions().colors[1]
								}
						},
						title: {
								text: 'Response Time',
								style: {
										color: Highcharts.getOptions().colors[1]
								}
						},
						opposite: true
						}, {
								gridLineWidth: 0,
								title: {
										text: 'Call',
										style: {
												color: Highcharts.getOptions().colors[0]
										}
								},
								labels: {
										format: '{value}',
										style: {
												color: Highcharts.getOptions().colors[0]
										}
								}
						}, {
							gridLineWidth: 0,
							title: {
									text: 'Concurrency',
									style: {
											color: Highcharts.getOptions().colors[2]
									}
							},
							labels: {
									format: '{value}',
									style: {
											color: Highcharts.getOptions().colors[2]
									}
							},
							opposite: true
						}, {
							gridLineWidth: 0,
							title: {
									text: 'QPS',
									style: {
											color: Highcharts.getOptions().colors[3]
									}
							},
							labels: {
									format: '{value}',
									style: {
											color: Highcharts.getOptions().colors[3]
									}
							},
							opposite: true
						}],
				tooltip: {
						shared: true
				},
				legend: {
						layout: 'vertical',
						align: 'right',
						x: -200,
						verticalAlign: 'top',
						y: 0,
						floating: true,
						backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
				},
				series: [{
						name: 'Calls',
						type: 'column',
						yAxis: 1,
						data: call,
						tooltip: {
							valueSuffix: ''
						}
				}, {
						name: 'Response Time',
						type: 'spline',
						yAxis: 2,
						data: rt,
						tooltip: {
							valueSuffix: ' ms'
						}
				},{
					name: 'Concurrency',
					type: 'spline',
					data: cc,
					dashStyle: 'Dash',
					tooltip: {
						valueSuffix: ''
					}
				},{
					name: 'QPS',
					type: 'spline',
					yAxis: 3,
					data: qps,
					dashStyle: 'LongDash',
					tooltip: {
						valueSuffix: ''
					}
				}]
			});
			map.put(divId, chart);
		}
		
	},

	loadApiStatisticChart: function(divId, title, categories, highestValues, middleValues, lowestValues, yTitle, tip){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			var data = [{
			        name: 'Highest Value',
			        data: highestValues
			    }, {
			        name: 'Middle Value',
			        data: middleValues
			    }, {
			        name: 'Lowest Value',
			        data: lowestValues
			    }];
			chart.update({
				xAxis: {
			        categories: categories,
			        allowDecimals: false
			    },
				series: data
			});
		}else{
			var chart = Highcharts.chart(divId, {
			    chart: {
			        type: 'area'
			    },
			    title: {
			        text: title
			    },
			    xAxis: {
			        categories: categories,
			        allowDecimals: false
			    },
			    yAxis: {
			        title: {
			            text: yTitle
			        }
			    },
				tooltip: {
			        pointFormat: '{series.name}: <b>{point.y:,.0f}</b>'
			    },
			    exporting: {
			        enabled: false
			    },
			    series: [{
			        name: 'Highest Value',
			        data: highestValues
			    }, {
			        name: 'Middle Value',
			        data: middleValues
			    }, {
			        name: 'Lowest Value',
			        data: lowestValues
			    }]
			});
			map.put(divId, chart);
		}
	}
}