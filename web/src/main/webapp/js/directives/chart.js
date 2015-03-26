angular.module('MonitorDirectives')
.directive('chart', ['d3Service', '$interval', '$http', function(d3Service, $interval, $http) {
  return {
    restrict: 'E',
    scope: {
      chartname: '@',
      chartwidth: '@',
      chartheight: '@'
    },
    link: function(scope, element, attrs) {

      // Once d3 is loaded we can start drawing the chart
      d3Service.d3().then(function(d3) {
        var margin = {top: 20, right: 20, bottom: 20, left: 50},
        width = (scope.chartwidth ? scope.chartwidth : 600) - margin.left - margin.right,
        height = (scope.chartheight ? scope.chartheight :  400) - margin.top - margin.bottom;

        var parseDate = d3.time.format("%Y-%m-%d %H:%M:%S").parse;

        var x = d3.time.scale()
          .range([0, width]);

        var y = d3.scale.linear()
          .range([height, 0]);

        var xAxis = d3.svg.axis()
          .scale(x)
          .ticks(5) // TODO: Should it really be 5 here? Maybe a few more?
          .orient("bottom")
          .tickFormat(d3.time.format("%H:%M"));

        var yAxis = d3.svg.axis()
          .scale(y)
          .orient("left");

        // Define the function that draws the area
        var area = d3.svg.area()
          .x(function(d) { return x(d.timeStamp); })
          .y0(height)
          .y1(function(d) { return y(d.count); });

        // Define the function that draws the line
        var line = d3.svg.line()
          .x(function(d) { return x(d.timeStamp); })
          .y(function(d) { return y(d.count); });

        // We create a div with the id of the chartname to refer to this
        // specific chart in case of several charts on page
        element.html("<div id='" + scope.chartname + "'></div>");

        var svg = d3.select("#" + scope.chartname).append("svg")
          .attr("width", width + margin.left + margin.right)
          .attr("height", height + margin.top + margin.bottom)
          .append("g")
          .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var data = [];

        x.domain(d3.extent(data, function(d) { return d.timeStamp; }));
        y.domain([0, d3.max(data, function(d) { return d.count; })]);

        svg.append("g").append("path")
          .datum(data)
          .attr("class", "area")
          .attr("d", area);

        svg.append("g").append("path")
          .datum(data)
          .attr("class", "line")
          .attr("d", line);

        svg.append("g")
          .attr("class", "x axis")
          .attr("transform", "rotate(90)")
          .attr("transform", "translate(0," + height + ")")
          .call(xAxis);

        svg.append("g")
          .attr("class", "y axis")
          .call(yAxis);

        svg.append("g").append("text")
          .attr("class", "headline")
          .attr("text-anchor", "middle")
          .attr("transform", "translate("+(width/2 - 15)+"," + (height/2 - 15) + ")");

        // Updates the data in the chart
        function updateChart(data) {

            data.map(function(d) {
              d.timeStamp = parseDate(d.timeStamp);
              d.count = +d.count;
            });
            x.domain(d3.extent(data, function(d) { return d.timeStamp; }));
            y.domain([0, d3.max(data, function(d) { return d.count; })]);

            var svg = d3.select("#" + scope.chartname).transition();

            svg.select(".headline")
              .duration(750)
              .text(function(d) {
                return Math.floor(data[data.length - 1].count); // TODO Not needed when we get real data?
              });
            svg.select(".line")
              .duration(750)
              .attr("d", line(data));
            svg.select(".area")
              .duration(750)
              .attr("d", area(data));
            svg.select(".x.axis")
              .duration(750)
              .call(xAxis);
            svg.select(".y.axis")
              .duration(750)
              .call(yAxis);
        }
        function fetchData() {
            $http.get('/api/counters/' + scope.chartname).
                success(function(data, status, headers, config) {
                    updateChart(data);
                }).
                error(function(data, status, headers, config) {
                    console.log('Could not fetch the number of logged in users from the server for service ' + scope.chartname);
                });
        }
        fetchData();
        // Update the chart with new data from server
        var timer = $interval(fetchData, 5000);
        scope.$on('$destroy', function() {
          if (timer) {
            $interval.cancel(timer);
          }
        });
      });
    }
  }
}]);
