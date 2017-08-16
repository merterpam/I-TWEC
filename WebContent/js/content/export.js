function loadExport()
{
	var mainSection = d3.select("#mainSection");
	mainSection.html('' 
+'			<div class="row"></br></div>\n'
+'		    <div class="col-md-4 offset-md-2">\n'
+'		        <a href="javascript:void(0)" class="btn btn-default" onclick="onDownload()">Download Clusters</a>\n'
+'	        <form method="post" action="downloadclusters">\n'
+'		            <input type="submit" id="downloadButton" style="visibility: hidden;">\n'
+'		        </form>\n'
+'		        <p>You can download clusters here. In the output file, each cluster is seperated by a new line and each cluster has a cluster label, cluster size and tweets which the cluster contains. If there are same tweets in the cluster, they are grouped and displayed in a single line.\n'
+'		    </div>\n'
+'          <div class="col-md-1"></div>\n'
+'		    <div class="col-md-4 offset-md-2">\n'
+'		        <a href="javascript:void(0)" class="btn btn-default" onclick="onEvaluate()">Download Evaluations</a>\n'
+'		        <form method="post" action="downloadevaluation">\n'
+'		            <input type="submit" id="evaluateButton" style="visibility: hidden;">\n'
+'		        </form>\n'
+'		        <p>You can download the cluster evaluations here. In the output file, each line represents a cluster. Each line has a cluster label, cluster size and intra-clustr evaluation score.\n'
+'		    </div>');
	var tableSection = d3.select("#table");
	tableSection.html("");

}

function onDownload()
{
	$(".overlay").show();
	$('#loading-text').html('Preparing clusters');
	$.ajax({
		url: 'uploadsentimentmerge',
		type: 'POST',
		data: 
		{
			loadProds: 1,
			responseData: JSON.stringify(sentimentResponse)
		},
		async: true,
		success: function (dataRaw) {
			$(".overlay").hide();
			$('#downloadButton').click();
		},
		error: function(xhr, textStatus, errorThrown){
			$(".overlay").hide();
			alert("Request Error: " + errorThrown); 
		},
		cache: false
	});
}

function onEvaluate()
{
	$(".overlay").show();
	$('#loading-text').html('Evaluating clusters');
	$.ajax({
		url: 'uploadsentimentmerge',
		type: 'POST',
		data: 
		{
			loadProds: 1,
			responseData: JSON.stringify(sentimentResponse)
		},
		async: true,
		success: function (dataRaw) {
			$(".overlay").hide();
			$('#evaluateButton').click();
		},
		error: function(xhr, textStatus, errorThrown){
			$(".overlay").hide();
			alert("Request Error: " + errorThrown); 
		},
		cache: false
	});
}