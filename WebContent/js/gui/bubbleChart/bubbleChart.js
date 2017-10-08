var BubbleChart;

  BubbleChart = (function() {
    function BubbleChart(data, chart, tableDiv) {
      this.data = data;
	  this.tableDiv = tableDiv;
	  this.chart = chart;
      this.width = 440;
      this.height = 350;
      this.tooltip = CustomTooltip("gates_tooltip", 240);
      this.center = {
        x: this.width / 2,
        y: this.height / 2
      };

      this.layout_gravity = -0.01;
      this.damper = 0.1;
      this.vis = null;
      this.nodes = [];
      this.force = null;
      this.circles = null;
      this.fill_color = d3.scale.ordinal().domain([0,1,2,3]).range(["#AAA", "#e4b7b2", "#ee9586", "#d84b2a"]);
      this.max_amount = d3.max(this.data, function(d) {
        return d.tweetSize;
      });
      this.radius_scale = d3.scale.pow().exponent(1).domain([10, d3.sum(data, function(d) { return d.tweetSize; })]).range([1,3600]);
      this.create_nodes();
      this.create_vis();
    }

    BubbleChart.prototype.create_nodes = function() {
	  var i = 0;
	  var dividend = this.max_amount/3;
    this.nodes = [];
      this.data.forEach((function(_this) {
        return function(d) {
          var node;
          node = {
            id: i++,
            radius: _this.radius_scale(d.tweetSize),
            value: d.tweetSize,
            name: d.label,
			group: Math.floor(d.tweetSize / dividend),
            x: Math.random() * 900,
            y: Math.random() * 800
          };
		  if(node.value > 9)
			_this.nodes.push(node);
        };
      })(this));
      return this.nodes.sort(function(a, b) {
        return b.value - a.value;
      });
    };

    BubbleChart.prototype.create_vis = function() {
      var that;
      this.vis = this.chart.append("svg").attr("width", this.width).attr("height", this.height).attr("id", "svg_vis");
      this.circles = this.vis.selectAll("circle").data(this.nodes, function(d) {
        return d.id;
      });
      that = this;
      this.circles.enter().append("circle").attr("r", 0).attr("fill", (function(_this) {
        return function(d) {
          return _this.fill_color(d.group);
        };
      })(this)).attr("stroke-width", 2).attr("stroke", (function(_this) {
        return function(d) {
          return d3.rgb(_this.fill_color(d.group)).darker();
        };
      })(this)).attr("id", function(d) {
        return "bubble_" + d.id;
      }).on("mouseover", function(d, i) {
        return that.show_details(d, i, this);
      }).on("mouseout", function(d, i) {
        return that.hide_details(d, i, this);
      }).on("click", function(d,i) {
		 return that.display_tweets(d, i, this); 
	  });
      return this.circles.transition().duration(2000).attr("r", function(d) {
        return d.radius;
      });
    };

    BubbleChart.prototype.charge = function(d) {
      return -Math.pow(d.radius, 2.0) / 8;
    };

    BubbleChart.prototype.start = function() {
      return this.force = d3.layout.force().nodes(this.nodes).size([this.width, this.height]);
    };

    BubbleChart.prototype.display_group_all = function() {
      this.force.gravity(this.layout_gravity).charge(this.charge).friction(0.9).on("tick", (function(_this) {
        return function(e) {
          return _this.circles.each(_this.move_towards_center(e.alpha)).attr("cx", function(d) {
            return d.x;
          }).attr("cy", function(d) {
            return d.y;
          });
        };
      })(this));
      this.force.start();
    };

    BubbleChart.prototype.move_towards_center = function(alpha) {
      return (function(_this) {
        return function(d) {
          d.x = d.x + (_this.center.x - d.x) * (_this.damper + 0.02) * alpha;
          return d.y = d.y + (_this.center.y - d.y) * (_this.damper + 0.02) * alpha;
        };
      })(this);
    };
	
	BubbleChart.prototype.display_tweets = function(dataL, i, element) {
		this.tableDiv.html('');
		var content = '<table class="table table-striped"> \n';
		var clusterElement = clusterResponse.clusters[dataL.id];
		for(var i = 0; i < 10 && i < clusterElement.elements.length; i++)
		{
			var tweet = clusterElement.elements[i].tweet.tweet;
			var count = clusterElement.elements[i].tweetSize;
			
			content += '<tr><td colspan="2">' + tweet + '</td><td>' + count + '</td></tr> \n'
		}
		content += '</table>';
		this.tableDiv.html(content);
		//content = 
	};

    BubbleChart.prototype.show_details = function(data, i, element) {
      var content;
      d3.select(element).attr("stroke", "black");
      content = "<span class=\"name\">Label:</span><span class=\"value\"> " + data.name + "</span><br/>";
      content += "<span class=\"name\">Size:</span><span class=\"value\"> " + data.value + "</span><br/>";
      return this.tooltip.showTooltip(content, d3.event);
    };

    BubbleChart.prototype.hide_details = function(data, i, element) {
      d3.select(element).attr("stroke", (function(_this) {
        return function(d) {
          return d3.rgb(_this.fill_color(d.group)).darker();
        };
      })(this));
      return this.tooltip.hideTooltip();
    };

    return BubbleChart;

  })();




