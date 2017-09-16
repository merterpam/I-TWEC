d3.chart = d3.chart || {};

/**
 * Dependency wheel chart for d3.js
 *
 * Usage:
 * var chart = d3.chart.dependencyWheel();
 * d3.select('#chart_placeholder')
 *   .datum({
 *      packageNames: [the name of the packages in the matrix],
 *      matrix: [your dependency matrix]
 *   })
 *   .call(chart);
 *
 * // Data must be a matrix of dependencies. The first item must be the main package.
 * // For instance, if the main package depends on packages A and B, and package A
 * // also depends on package B, you should build the data as follows:
 *
 * var data = {
 *   packageNames: ['Main', 'A', 'B'],
 *   matrix: [[0, 1, 1], // Main depends on A and B
 *            [0, 0, 1], // A depends on B
 *            [0, 0, 0]] // B doesn't depend on A or Main
 * };
 *
 * // You can customize the chart width, margin (used to display package names),
 * // and padding (separating groups in the wheel)
 * var chart = d3.chart.dependencyWheel().width(700).margin(150).padding(.02);
 *
 * @author Fran�ois Zaninotto
 * @license MIT
 * @see https://github.com/fzaninotto/DependencyWheel for complete source and license
 */
d3.chart.dependencyWheel = function (options) {

    var width = 1100;
    var height = 700;
    var margin = 150;
    var padding = 0.02;

    var clickedIndex = -1;
    var clickedGroup = [];

    function chart(selection) {
        selection.each(function (data) {

            clickedIndex = -1;

            var matrix = data.matrix;
            var packageNames = data.labels;
            var radius = Math.min(width, height) / 2 - margin;

            // create the layout
            var chord = d3.layout.chord()
                .padding(padding)
                .sortSubgroups(d3.descending);

            // Select the svg element, if it exists.
            var svg = d3.select(this).selectAll("svg").data([data]);

            // Otherwise, create the skeletal chart.
            var gEnter = svg.enter().append("svg:svg")
                .attr("width", width)
                .attr("height", height)
                .attr("class", "dependencyWheel")
                .append("g")
                .attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");

            var arc = d3.svg.arc()
                .innerRadius(radius)
                .outerRadius(radius + 20);

            var fill = function (d) {
                if (d.index === 0) return '#ccc';
                return "hsl(" + parseInt(((packageNames[d.index].label[0].charCodeAt() - 97) / 26) * 360, 10) + ",90%,70%)";
            };

            // Returns an event handler for fading a given chord group.
            var fade = function (opacity) {
                return function (g, i) {
                    if (clickedIndex == -1) {
                        svg.selectAll(".chord")
                            .filter(function (d) {
                                return d.source.index != i && d.target.index != i;
                            })
                            .transition()
                            .style("opacity", opacity);
                        var groups = [];
                        svg.selectAll(".chord")
                            .filter(function (d) {
                                if (d.source.index == i) {
                                    groups.push(d.target.index);
                                }
                                if (d.target.index == i) {
                                    groups.push(d.source.index);
                                }
                            });
                        clickedGroup = groups;
                        groups.push(i);
                        var length = groups.length;
                        svg.selectAll('.group')
                            .filter(function (d) {
                                for (var i = 0; i < length; i++) {
                                    if (groups[i] == d.index) return false;
                                }
                                return true;
                            })
                            .transition()
                            .style("opacity", opacity);
                    }
                    ;
                }
            };

            var clickAndMerge = function () {
                return function (g, i) {
                    if (clickedIndex == -1) {
                        clickedIndex = i;
                        $('#sentimentFirst').html(svg.selectAll("text")[0][i].innerHTML);
                        $('#sentimentFirst').attr('index', preparedData.labels[i].index);
                        $('#sentimentFirst').show();
                    }
                    else if (clickedIndex == i) {
                        clickedIndex = -1;
                        clickedGroup = [];

                        $('#sentimentFirst').hide();
                        $('#sentimentSecond').hide();
                        $('#sentimentSubmit').hide();
                    } else {
                        var length = clickedGroup.length;
                        for (var j = 0; j < length; j++) {
                            if (clickedGroup[j] == i) {
                                $('#sentimentSecond').html(svg.selectAll("text")[0][i].innerHTML);
                                $('#sentimentSecond').attr('index', preparedData.labels[i].index);
                                $('#sentimentSecond').show();
                                $('#sentimentSubmit').show();
                                break;
                            }
                        }
                    }
                }
            };

            chord.matrix(matrix);

            var rootGroup = chord.groups()[0];
            //var rotation = - (rootGroup.endAngle - rootGroup.startAngle) / 2 * (180 / Math.PI);

            var g = gEnter.selectAll("g.group")
                .data(chord.groups)
                .enter().append("svg:g")
                .attr("class", "group");

            g.append("svg:path")
                .style("fill", fill)
                .style("stroke", fill)
                .attr("d", arc)
                .on("mouseover", fade(0.1))
                .on("mouseout", fade(1))
                .on("click", clickAndMerge());

            g.append("svg:polyline")
                .each(function (d) {
                    d.angle = (d.startAngle + d.endAngle) / 2;
                })
                .attr("points", function (d) {
                    var lineAngle = d.angle - Math.PI / 2;
                    var marginAngle = -Math.PI - lineAngle;

                    var xStart = (radius + 13) * Math.cos(lineAngle);
                    var yStart = (radius + 13) * Math.sin(lineAngle);

                    var xMiddle = 30 * Math.cos(lineAngle) + xStart;
                    var yMiddle = 30 * Math.sin(lineAngle) + yStart;

                    var xEnd = xMiddle + 10 * Math.abs(Math.sin(lineAngle)) * (Math.cos(lineAngle) > 0 ? 1 : -1);
                    var yEnd = yMiddle;
                    return Math.floor(xStart) + "," + Math.floor(yStart) + " " + Math.floor(xMiddle) + "," + Math.floor(yMiddle) + " " + Math.floor(xEnd) + "," + Math.floor(yEnd);
                })

            g.append("svg:text")
                .each(function (d) {
                    d.angle = (d.startAngle + d.endAngle) / 2;
                })
                .attr("dy", ".35em")
                .attr("text-anchor", function (d) {
                    return d.angle > Math.PI ? "end" : null;
                })
                .attr("transform", function (d) {
                    var rotAngle = ((d.angle * 180 / Math.PI - 90) + 360) % 360;
                    var marginAngle = (270 - rotAngle + 360) % 360;
                    var textAngle = (marginAngle + 90) % 360;
                    var marginCos = Math.cos(Math.PI * marginAngle / 180);
                    var margin = Math.pow(marginCos, 1) * 30;
                    return "rotate(" + rotAngle + ")" +
                        "translate(" + (radius + 26) + ")" +
                        "rotate(" + marginAngle + ")" +
                        "translate(" + margin + ")" +
                        "rotate(" + 90 + ")";
                })
                .text(function (d) {
                    return packageNames[d.index].label;
                });

            gEnter.selectAll("path.chord")
                .data(chord.chords)
                .enter().append("svg:path")
                .attr("class", "chord")
                .style("stroke", function (d) {
                    return d3.rgb(fill(d.source)).darker();
                })
                .style("fill", function (d) {
                    return fill(d.source);
                })
                .attr("d", d3.svg.chord().radius(radius))
                .style("opacity", 1);
        });
    }

    chart.height = function (value) {
        if (!arguments.length) return height;
        height = value;
        return chart;
    };

    chart.width = function (value) {
        if (!arguments.length) return width;
        width = value;
        return chart;
    };

    chart.margin = function (value) {
        if (!arguments.length) return margin;
        margin = value;
        return chart;
    };

    chart.padding = function (value) {
        if (!arguments.length) return padding;
        padding = value;
        return chart;
    };

    return chart;
};