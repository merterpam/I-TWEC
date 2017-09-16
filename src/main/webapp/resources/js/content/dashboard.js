var bubbleChart, barChart, controlGUI;

var DashboardData = function () {
    this.clusterThreshold = 0.4;
    this.apply = function () {
        reClusterSend();
    };
};

var dashBoardData = new DashboardData();

function loadDashboard() {
    var mainSection = d3.select("#mainSection");
    mainSection.html("");
    var tableDiv = d3.select("#table");
    tableDiv.html("");

    var bubbleDiv = mainSection.append("div")
        .attr("class", "col-xs-3 col-sm-5")
        .attr("id", "bubbleChart");
    var barDiv = mainSection.append("div")
        .attr("class", "col-xs-3 col-sm-5")
        .attr("id", "barChart");

    var datGUIDiv = mainSection.append("div")
        .attr("class", "col-xs-3 col-sm-2")
        .attr("id", "dat_GUI");
    var baseDiv = d3.select("#baseDiv");


    if (bubbleChart == null) {
        bubbleChart = new BubbleChart(clusterResponse.clusters, bubbleDiv, tableDiv);
        bubbleChart.start();
        bubbleChart.display_group_all();
    }
    else {
        bubbleChart.chart = bubbleDiv;
        bubbleChart.tableDiv = tableDiv;
        bubbleChart.create_nodes();
        bubbleChart.create_vis();
        bubbleChart.start();
        bubbleChart.display_group_all();
    }


    if (barChart == null)
        barChart = new BarChart(clusterResponse.clusters, barDiv);
    else {
        barChart.chart = barDiv;
    }
    barChart.create_gui();

    if (controlGUI == null) {
        controlGUI = new dat.GUI({
            autoplace: false
        });
        controlGUI.add(dashBoardData, 'clusterThreshold').onChange(function (newValue) {
            document.getElementById("clusterThreshold").value = newValue;
        });

        controlGUI.add(dashBoardData, 'apply');
        controlGUI.close();
    }
    $('#dat_GUI').append($(controlGUI.domElement));
}

function reClusterSend() {
    var formData =
        {
            "clusterLimit": sentimentData.displayedClusterSize,
            "clusterThreshold": dashBoardData.clusterThreshold,
            "sentimentThreshold": sentimentData.sentimentThreshold,
            "shortTextLength": sentimentData.shortTextThreshold,
            "embeddingDimension": 100
        };

    d3.select("#mainSection").html('');
    $(".overlay").show();
    $('#loading-text').html('Please wait');
    $.ajax({
        url: 'uploadClusterThreshold',
        type: 'POST',
        data: formData,
        async: true,
        success: function (dataRaw) {
            clusterResponse = dataRaw;
            bubbleChart.data = clusterResponse.clusters;
            barChart.data = clusterResponse.clusters;
            $(".overlay").hide();
            loadDashboard();
            loadSentimentData();
        },
        error: function (xhr, textStatus, errorThrown) {
            $(".overlay").hide();
            alert("Request Error: " + errorThrown);
        },
        cache: false
    });
}
