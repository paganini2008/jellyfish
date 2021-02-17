var map = new Map();

var SummaryChartUtils = {
		
	loadCallChart: function(divId, title, data){
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
}

var SequenceChartUtils = {

	loadCallChart: function(divId, title, categories, successCount, failedCount, timeoutCount){
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
			        tickmarkPlacement: 'on',
			        title: {
			            enabled: false
			        }
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
				        tickmarkPlacement: 'on',
				        title: {
				            enabled: false
				        }
					},
				    yAxis: {
				        title: {
				            text: 'Calls'
				        },
				        labels: {
				            formatter: function () {
				                return this.value / 1000;
				            }
				        }
				    },
				    tooltip: {
				        split: true,
				        valueSuffix: ' calls'
				    },
				    plotOptions: {
				        area: {
				            stacking: 'normal',
				            lineColor: '#666666',
				            lineWidth: 1,
				            marker: {
				                lineWidth: 1,
				                lineColor: '#666666'
				            }
				        }
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
	
	loadComplexChart: function(divId, title, categories, call, rt, qps){
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
									color: Highcharts.getOptions().colors[2]
							}
					},
					title: {
							text: 'Response Time',
							style: {
									color: Highcharts.getOptions().colors[2]
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
							text: 'QPS',
							style: {
									color: Highcharts.getOptions().colors[1]
							}
					},
					labels: {
							format: '{value}',
							style: {
									color: Highcharts.getOptions().colors[1]
							}
					},
					opposite: true
			}],
			tooltip: {
					shared: true
			},
			legend: {
					layout: 'vertical',
					align: 'left',
					x: 80,
					verticalAlign: 'top',
					y: 55,
					floating: true,
					backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
			},
			series: [{
					name: 'Calls',
					type: 'column',
					yAxis: 1,
					data: call,
					tooltip: {
						valueSuffix: ' calls'
					}
			}, {
					name: 'Response Time',
					type: 'spline',
					yAxis: 2,
					data: rt,
					marker: {
						enabled: false
					},
					tooltip: {
						valueSuffix: ' ms'
					}
			}, {
					name: 'QPS',
					type: 'spline',
					data: qps,
					tooltip: {
						valueSuffix: ''
					}
			}]
		});
	},

	loadStatisticChart: function(divId, title, categories, highestValues, middleValues, lowestValues, yTitle, tip){
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
			        align: 'right',
			        verticalAlign: 'top',
			        x: -50,
			        y: 0,
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
			        valueSuffix: tip
			    },
			    plotOptions: {
			        areaspline: {
			            fillOpacity: 0.5
			        }
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