var BarChart;

BarChart = (function () {

    function BarChart(data, chart) {
        this.data = data;
        this.chart = chart;
        this.width = 440;
        this.height = 350;

    }

    BarChart.prototype.create_gui = function () {
        var svg = this.chart.append("svg").attr("width", this.width).attr("height", this.height).attr("id", "svg_vis"),
            margin = {
                top: 20,
                right: 20,
                bottom: 30,
                left: 40
            },
            width = +svg.attr("width") - margin.left - margin.right,
            height = +svg.attr("height") - margin.top - margin.bottom;

        var barWidth = 13;
        var totalBar = barWidth * this.data.length;
        var zoomedWidthBand = width / totalBar;
        var zoomEvent = d3.behavior.zoom().scaleExtent([1, 10]).on("zoom", zoom);
        var xScale = d3.scale.ordinal()
            .rangeBands([0, totalBar], 0.1, 0.5);
        yScale = d3.scale.linear()
            .range([height, 0]);
        svg.call(zoomEvent);

        var g = svg.append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var plotArea = g.append('g')
            .attr('clip-path', 'url(#plotAreaClip)');

        plotArea.append('clipPath')
            .attr('id', 'plotAreaClip')
            .append('rect')
            .attr({
                width: width,
                height: height + margin.top + margin.bottom
            });

        var xAxis = d3.svg.axis().scale(xScale).orient("bottom");
        var yAxis = d3.svg.axis().scale(yScale).orient("left");

        var xScaleDom = [];
        for (i = 1; i < this.data.length + 1; i++) {
            xScaleDom.push(i);
        }
        xScale.domain(xScaleDom);
        yScale.domain([0, d3.max(this.data, function (d) {
            return d.tweetSize;
        })]);

        plotArea.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        g.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("y", -15)
            .attr("x", 40)
            .attr("dy", "0.71em")
            .attr("text-anchor", "end")
            .text("Cluster Size");

        var i = 1;
        bars = plotArea.selectAll(".bar")
            .data(this.data)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("x", function (d) {
                return xScale(i++);
            })
            .attr("y", function (d) {
                return yScale(d.tweetSize);
            })
            .attr("width", xScale.rangeBand())
            .attr("height", function (d) {
                return height - yScale(d.tweetSize);
            });

        //bar.call(d3.behavior.zoom().x(xScale).scaleExtent([1, 8]).on("zoom", zoom));


        function zoom() {

            var e = d3.event,
                tx = Math.min(0, Math.max(e.translate[0], width - totalBar * e.scale)),
                ty = Math.min(0, Math.max(e.translate[1], height - height * e.scale));
            zoomEvent.translate([tx, ty]);
            bars.attr("transform", "translate(" + tx + ",0)scale(" + d3.event.scale + ",1)");
            svg.select(".x.axis").attr("transform", "translate(" + tx + "," + (height) + ")")
                .call(xAxis.scale(xScale.rangeBands([0, totalBar * d3.event.scale], .1 * d3.event.scale, 0.5 * d3.event.scale)));
        }
    };

    return BarChart;
})();
