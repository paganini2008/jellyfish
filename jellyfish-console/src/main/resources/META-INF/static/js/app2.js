var map = new Map();

var SummaryChartUtils = {
		
	loadStatisticChart: function(divId, title, max, values, unit){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			chart.update({
				series: [{
			        name: title,
			        data: values,
			        dataLabels: {
			            format: '<div style="text-align:center"><span style="font-size:25px;color:' +
			            ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}</span><br/>' +
			            '<span style="font-size:12px;color:silver">' + unit + '</span></div>'
			        },
			        tooltip: {
			            valueSuffix: unit
			        }
			    }]
			});
		}else{
			var chart = Highcharts.chart(divId, {
				chart: {
			        type: 'solidgauge'
			    },
			    title: null,
			    pane: {
			        center: ['50%', '85%'],
			        size: '140%',
			        startAngle: -90,
			        endAngle: 90,
			        background: {
			            backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
			            innerRadius: '60%',
			            outerRadius: '100%',
			            shape: 'arc'
			        }
			    },
			    tooltip: {
			        enabled: true
			    },
			    yAxis: {
			        stops: [
			            [0.2, '#55BF3B'], // green
			            [0.5, '#DDDF0D'], // yellow
			            [0.9, '#DF5353'] // red
			        ],
			        lineWidth: 0,
			        minorTickInterval: null,
			        tickPixelInterval: 100,
			        tickWidth: 0,
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
			            format: '<div style="text-align:center"><span style="font-size:25px;color:' +
			            ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}</span><br/>' +
			            '<span style="font-size:12px;color:silver">' + unit + '</span></div>'
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
					name: 'Brands',
					colorByPoint: true,
					data: [{
							name: '1xx Count',
							y: countOf1xx
					}, {
						name: '2xx Count',
						y: countOf2xx,
						sliced: true,
						selected: true
					}, {
							name: '3xx Count',
							y: countOf3xx
					}, {
						name: '4xx Count',
						y: countOf4xx
					}, {
						name: '5xx Count',
						y: countOf5xx
					}]
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
										enabled: true
									},
									showInLegend: true
							}
					},
					series: [{
							name: 'Brands',
							colorByPoint: true,
							data: [{
								name: '1xx Count',
								y: countOf1xx
							}, {
								name: '2xx Count',
								y: countOf2xx,
								sliced: true,
								selected: true
							}, {
								name: '3xx Count',
								y: countOf3xx
							}, {
								name: '4xx Count',
								y: countOf4xx
							}, {
								name: '5xx Count',
								y: countOf5xx
							}]
					}]
			});
			map.put(divId, chart);
		}
	},
		
	loadCallCountChart: function(divId, title, successCount, failedCount, timeoutCount){
		if(map.containsKey(divId)){
			var chart = map.get(divId);
			chart.update({
				series: [{
					name: 'Brands',
					colorByPoint: true,
					data: [{
							name: 'Success Count',
							y: successCount,
							sliced: true,
							selected: true
					}, {
							name: 'Failed Count',
							y: failedCount
					}, {
							name: 'Timeout Count',
							y: timeoutCount
					}]
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
										enabled: true
									},
									showInLegend: true
							}
					},
					series: [{
							name: 'Brands',
							colorByPoint: true,
							data: [{
									name: 'Success Count',
									y: successCount,
									sliced: true,
									selected: true
							}, {
									name: 'Failed Count',
									y: failedCount
							}, {
									name: 'Timeout Count',
									y: timeoutCount
							}]
					}]
			});
			map.put(divId, chart);
		}
	}
}

var SequenceChartUtils = {

	loadCallCountChart: function(divId, title, categories, successCount, failedCount, timeoutCount){
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