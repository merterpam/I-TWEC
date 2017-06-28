function loadExport()
{
	var mainSection = d3.select("#mainSection");
	mainSection.html('<form method="post" action="downloadclusters"> \n <input type="submit" id="downloadButton" style="visibility: hidden;"/> \n </form> \n <a href="javascript:void(0)" class="btn btn-default" onclick="onDownload()">Download Clusters</a>  \n <a href="javascript:void(0)" class="btn btn-default" onclick="onEvaluate()">Download Evaluations</a> \n <form method="post" action="downloadevaluation"> \n <input type="submit" id="evaluateButton" style="visibility: hidden;"/> \n </form> ');
	var tableSection = d3.select("#table");
	tableSection.html("");

}

function onDownload()
{
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
			console.log("success");
			$('#downloadButton').click();
		},
		cache: false
	});
}

function onEvaluate()
{
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
			console.log("success");
			$('#evaluateButton').click();
		},
		cache: false
	});
}