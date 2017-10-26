var SentimentData = function () {
    this.sentimentThreshold = 0.8;
    this.shortTextThreshold = 15;
    this.displayedClusterSize = 100;
    this.apply = function () {
        calculateSentimentSend();
    };

    this.refresh = function () {
        onSentimentRefresh();
    };
};

var preparedData;
var sentimentData = new SentimentData();
var chart, controlGUI;

function loadSentiment() {
    var mainSection = d3.select("#mainSection");
    mainSection.html("");
    var tableSection = d3.select("#table");
    tableSection.html("");

    var sentimentWheel = mainSection.append("div")
        .attr("class", "col-xs-6 col-sm-10")
        .attr("id", "sentimentWheel");

    var datGUI = mainSection.append("div")
        .attr("class", "col-xs-6 col-sm-2")
        .attr("id", "dat_GUI");

    if (chart == null)
        chart = d3.chart.dependencyWheel().margin(100).width(1100).height(500);

    preparedData = prepareData(sentimentData.sentimentThreshold);
    sentimentWheel.datum(preparedData).call(chart);


    if (controlGUI == null) {
        var controlGUI = new dat.GUI({
            autoplace: false
        });


        var interactive = controlGUI.addFolder('Interactive');
        interactive.add(sentimentData, 'sentimentThreshold', 0.5, 1).onFinishChange(function (newValue) {
            sentimentWheel.html('');
            preparedData = prepareData(sentimentData.sentimentThreshold);
            sentimentWheel.datum(preparedData).call(chart)
        });

        interactive.open();

        var processing = controlGUI.addFolder("Processing");
        processing.add(sentimentData, 'shortTextThreshold');
        processing.add(sentimentData, 'displayedClusterSize');
        processing.add(sentimentData, 'apply');
        processing.add(sentimentData, 'refresh');

        processing.open();

        controlGUI.close();

    }
    $('#dat_GUI').append($(controlGUI.domElement));


}

function prepareData(threshold) {
    var copyResponse = jQuery.extend(true, {}, sentimentResponse);
    var size = copyResponse.labels.length;
    for (var i = 0; i < size; i++) {
        var notEmpty = false;
        for (var j = 0; j < size && !copyResponse.labels[i].merged; j++) {
            if (copyResponse.matrix[i][j] < threshold || copyResponse.labels[j].merged) {
                copyResponse.matrix[i][j] = 0;
            } else if (!notEmpty) {
                notEmpty = true;
            }
        }

        if (!notEmpty || copyResponse.labels[i].merged) {
            copyResponse.labels.splice(i, 1);
            copyResponse.matrix.splice(i, 1);
            size--;

            for (var j = 0; j < size; j++) {
                copyResponse.matrix[j].splice(i, 1);
            }

            i--;
        }
    }

    return copyResponse;
}

function onSentimentSubmit() {
    var firstIndex = $('#sentimentFirst').attr('index');
    var secondIndex = $('#sentimentSecond').attr('index');

    var clusterIndex = sentimentResponse.labels[secondIndex].clusterIndex;
    sentimentResponse.labels[secondIndex].merged = true;

    sentimentResponse.labels[firstIndex].mergedIndexes.push(clusterIndex);
    sentimentResponse.mergeOperation = true;
    $('#sentimentFirst').hide();
    $('#sentimentSecond').hide();
    $('#sentimentSubmit').hide();

    var sentimentWheel = d3.select('#sentimentWheel');

    sentimentWheel.html('');
    preparedData = prepareData(sentimentData.sentimentThreshold);
    sentimentWheel.datum(preparedData).call(chart);

}

function calculateSentimentSend() {
    var formData = {
        "clusterThreshold": dashBoardData.clusterThreshold,
        "clusterLimit": sentimentData.displayedClusterSize,
        "shortTextLength": sentimentData.shortTextThreshold,
        "embeddingDimension": 100
    };

    d3.select("#mainSection").html('');
    $(".overlay").show();
    $('#loading-text').html('Please wait');
    $.ajax({
        url: 'calculateSentiment',
        type: 'POST',
        data: formData,
        async: true,
        success: function (dataRaw) {
            sentimentResponse = dataRaw;
            $(".overlay").hide();
            loadSentiment();
        },
        error: function (xhr, textStatus, errorThrown) {
            $(".overlay").hide();
            alert("Request Error: " + errorThrown);
        },
        cache: false
    });
}

function onSentimentRefresh() {
    var formData =
        {};


    d3.select("#mainSection").html('');
    $(".overlay").show();
    $('#loading-text').html('Please wait');
    $.ajax({
        url: 'refreshsentimentmerge',
        type: 'POST',
        data:
            {
                loadProds: 1,
                clusterLimit: sentimentData.displayedClusterSize,
                shortTextLength: sentimentData.shortTextThreshold,
                embeddingDimension: 100,
                responseData: JSON.stringify(sentimentResponse)
            },
        async: true,
        success: function (dataRaw) {
            sentimentResponse = dataRaw;
            $(".overlay").hide();
            loadSentiment();
        },
        error: function (xhr, textStatus, errorThrown) {
            $(".overlay").hide();
            alert("Request Error: " + errorThrown);
        },
        cache: false
    });
}