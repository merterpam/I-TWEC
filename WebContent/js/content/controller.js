var gId;

$(function() {
	loadDynamicBar(0);
	gId = 0;

	$('#fileLoad').change(function() {

		var filename = $('#fileLoad').val();
		if (filename.substring(3, 11) == 'fakepath') {
			filename = filename.substring(12);
		} // Remove c:\fake at beginning from localhost chrome
		$('#filename').html(filename);

		$('#fileSubmit').html('<a href="" class="btn btn-default">Upload</a>');
	});

	$('#fileSubmit').click(function() {
		$('#fileForm').submit();
		return false;
	});

});

function loadDynamicBar(id) {

	var parentElement = document.getElementById('navDynamicBar');
	var linkArray = ['Dashboard', 'Semantic Relatedness', 'Export'];
	//var linkArray = ['Dashboard', 'Semantic Relatedness', 'Reports', 'Analytics', 'Export'];

	var innerContent = '';
	for (i = 0; i < linkArray.length; i++) {
		if (i == id) {
			innerContent += '<li onclick="loadPage(' + i + ')" class="active"><a>' + linkArray[i] + '<span class="sr-only">(current)</span></a></li> \n';
		} else {
			innerContent += '<li onclick="loadPage(' + i + ')"><a>' + linkArray[i] + '</a></li> \n';
		}
	}
	parentElement.innerHTML = innerContent;
}

function loadPage(id) {
	if (clusterResponse != null) {
		loadDynamicBar(id);
		if (id != gId) {
			if (id == 0) //Dashboard
			{
				loadDashboard();
				$('#fileSubmit').show();
				gId = 0;
			} else if (id == 1) {
				if (sentimentResponse != null)
					loadSentiment();
				else {
					d3.select("#mainSection").html('');
					d3.select("#table").html('');
					$(".overlay").show();
				}
				gId = 1;
				$('#fileSubmit').hide();
			} else if (id == 2) {
				//window.location = 'downloadfile';
				loadExport();
				$('#fileSubmit').hide();
				gId = 2;
			}
		}
	}
}

function onLoad(element) {
	$('#fileLoad').click();
	element.blur();
	return false;
}

$("#fileForm").submit(
	function onSubmit() {

		d3.select("#mainSection").html('');
		$(".overlay").show();
		var formData = new FormData(this);
		$.ajax({
			url: 'uploadfile',
			type: 'POST',
			data: formData,
			async: true,
			success: function(dataRaw) {
				clusterResponse = JSON.parse(dataRaw);
				$(".overlay").hide();

				loadDashboard();
				loadSentimentData();

			},
			cache: false,
			contentType: false,
			processData: false
		});

		return false;
	});

function loadSentimentData() {
	$.ajax({
		url: 'loadsentiment',
		type: 'POST',
		async: true,
		success: function(dataRaw) {
			sentimentResponse = JSON.parse(dataRaw);
			if (gId == 1) {
				loadSentiment();
				$(".overlay").hide();
			}
		},
		cache: false,
		contentType: false,
		processData: false
	});

	return false;
}