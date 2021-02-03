$.fn.parseForm = function(){
	    var serializeObj = {};
	    var array = this.serializeArray();
	    var str = this.serialize();
	    $(array).each(function(){
	        if(serializeObj[this.name]){
	            if($.isArray(serializeObj[this.name])){
	                serializeObj[this.name].push(this.value);
	            }else{
	                serializeObj[this.name]=[serializeObj[this.name],this.value];
	            }
	        }else{
	            serializeObj[this.name]=this.value; 
	        }
	    });
	    return serializeObj;
	};

$(function(){
		
	$('#searchBtn').click(function(){
		doSearch();
		return false;
	});
	
	$('#searchHistoryBtn').click(function(){
		doSearchAndAppend();
		return false;
	});
	
	$('#loadMore').live('click', function(){
		var nextPage =  $(this).attr('next-page');
		$('#page').val(nextPage);
		doSearchAndAppend();
	});

});

var scrollState = 'down';
var paging = false;

function doSearch(){
		var obj = $('#searchFrm');
		var url = obj.attr('action');
		var param = $('#searchFrm').parseForm();
		$.ajax({
			    url: url,
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				dataType:'json',
				data: JSON.stringify(param),
				success: function(data){
					var log = 'No search result';
				    if(data.data.results!=null && data.data.results.length > 0){
				    	log = '';
				    	$.each(data.data.results, function(i, item){
				    		var str = 'clusterName=' + item.clusterName + ', applicationName=' + item.applicationName + ', host=' + item.host + ', identifier=' + item.identifier;
				    		if(item.marker && item.marker.length > 0){
				    			str += ', marker=' + item.marker;
				    		}
							var logEntry = '<div class="logEntry"><pre>';
							logEntry += '<font color="#FF0000"><b>[logEntry[' + str + ']]: </b></font>';
							logEntry += item.datetime + ' <b class="' + item.level.toLowerCase() + '">[' + item.level.toUpperCase() + ' ]</b> ' + item.loggerName + ' - ' + item.message;
							if(item.stackTraces.length > 0){
								logEntry += '<br />';
								$.each(item.stackTraces, function(j, stackTrace){
									logEntry += stackTrace + '<br/>';
								});
							}
							logEntry += '</pre></div>';
							log += logEntry;
						});
				    }
				    if(log.length > 0){
				    	$('#logBox').html(log);
				    	if(scrollState == 'down'){
				    		if($("input[name='asc']:checked").val() == 'true'){
				            	$('#logBox').scrollTop($('#logBox')[0].scrollHeight - 10);
							}
				    	}
				    }
				}
			});
}
	
function doSearchAndAppend(){
		if(paging){
			return;
		}
		paging = true;
		var obj = $('#searchFrm');
		var url = obj.attr('action');
		var pageNo = parseInt($('#page').val());
		var param = $('#searchFrm').parseForm();
		$.ajax({
			    url: url + '?page=' + pageNo,
				type:'post',
				contentType: 'application/json;charset=UTF-8',
				dataType:'json',
				data: JSON.stringify(param),
				success: function(data){
					var page = data.data;
					var rowData = page.results;
					var log = '';
				    if(rowData != null && rowData.length > 0){
				    	$.each(rowData, function(i, item){
				    		var str = 'clusterName=' + item.clusterName + ', applicationName=' + item.applicationName + ', host=' + item.host + ', identifier=' + item.identifier;
				    		if(item.marker && item.marker.length > 0){
				    			str += ', marker=' + item.marker;
				    		}
							var logEntry = '<div class="logEntry"><pre>';
							logEntry += '<font color="#FF0000"><b>[logEntry[' + str + ']]: </b></font>';
							logEntry += item.datetime + ' <b class="' + item.level.toLowerCase() + '">[' + item.level.toUpperCase() + ' ]</b> ' + item.loggerName + ' - ' + item.message;
							if(item.stackTraces.length > 0){
								logEntry += '<br />';
								$.each(item.stackTraces, function(j, stackTrace){
									logEntry += stackTrace + '<br/>';
								});
							}
							logEntry += '</pre></div>';
							log += logEntry;
						});
				    }
				    if(log.length > 0){
				    	$('#loadMore').remove();
				    	log += '<div id="loadMore" next-page="' + (page.page < page.totalPages ? page.nextPage:page.totalPages + 1) + '">&lt;click and load more&gt;</div>';
				    	$('#logBox').append(log);
				    }
				    paging = false;
				}
			});
}