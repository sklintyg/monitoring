angular.module('MonitorDirectives')
.directive('chart', ['d3Service', '$interval', function(d3Service, $interval) {
  return {
    restrict: 'E',
    scope: {
      chartname: '@',
      chartwidth: '@',
      chartheight: '@'
    },
    link: function(scope, element, attrs) {
      // TODO replace this with accuiring proper data from server
      // Gets the data from the server
      function fetchData() {
        return [
        {
          "date": "2015-01-10:10:10:00",
          "users": Math.random() * 100
        },
        {
          "date": "2015-01-10:10:11:00",
          "users": Math.random() * 100
        },
        {
          "date": "2015-01-10:10:12:00",
          "users": Math.random() * 100
        },
        {
          "date": "2015-01-10:10:13:00",
          "users": Math.random() * 100
        },
        {
          "date": "2015-01-10:10:14:00",
          "users": Math.random() * 100
        },
        {
          "date": "2015-01-10:10:15:00",
          "users": Math.random() * 100
        },
        {
          "date": "2015-01-10:10:16:00",
          "users": Math.random() * 100
        },
        {
          "date": "2015-01-10:10:17:00",
          "users": Math.random() * 100
        }
        ];
      };

      // Once d3 is loaded we can start drawing the chart
      d3Service.d3().then(function(d3) {
        var margin = {top: 20, right: 20, bottom: 20, left: 30},
        width = (scope.chartwidth ? scope.chartwidth : 600) - margin.left - margin.right,
        height = (scope.chartheight ? scope.chartheight :  400) - margin.top - margin.bottom;

        var parseDate = d3.time.format("%Y-%m-%d:%H:%M:%S").parse;

        var x = d3.time.scale()
          .range([0, width]);

        var y = d3.scale.linear()
          .range([height, 0]);

        var xAxis = d3.svg.axis()
          .scale(x)
          .ticks(5) // TODO: Should it really be 5 here? Maybe a few more?
          .orient("bottom");

        var yAxis = d3.svg.axis()
          .scale(y)
          .orient("left");

        // Define the function that draws the area
        var area = d3.svg.area()
          .x(function(d) { return x(d.date); })
          .y0(height)
          .y1(function(d) { return y(d.users); });

        // Define the function that draws the line
        var line = d3.svg.line()
          .x(function(d) { return x(d.date); })
          .y(function(d) { return y(d.users); });

        // We create a div with the id of the chartname to refer to this
        // specific chart in case of several charts on page
        element.html("<div id='" + scope.chartname + "'></div>");

        var svg = d3.select("#" + scope.chartname).append("svg")
          .attr("width", width + margin.left + margin.right)
          .attr("height", height + margin.top + margin.bottom)
          .append("g")
          .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var data = fetchData();
        // Parse the data from the server
        data.map(function(d) {
          d.date = parseDate(d.date);
          d.users = +d.users;
        });

        x.domain(d3.extent(data, function(d) { return d.date; }));
        y.domain([0, d3.max(data, function(d) { return d.users; })]);

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
          .attr("transform", "translate("+(width/2 - 15)+"," + (height/2 - 15) + ")")
          .text(function(d) {
            return Math.floor(data[data.length - 1].users); // TODO Not needed when we get real data?
          });

        var counter = 0;

        // Updates the data in the chart
        function updateChart() {

            counter++;
            //var data = fetchData();
            //data.map(function(d) {
              //d.date = parseDate(d.date);
              //d.users = +d.users;
            //});
            data = data.slice(1, data.length);
            data.push({
              date: d3.time.minute.offset(data[data.length - 1].date, 1),
              users: Math.min(Math.max(data[data.length - 1].users + (Math.random() * 20 - 10), 0), 100)
            });
            x.domain(d3.extent(data, function(d) { return d.date; }));
            y.domain([0, d3.max(data, function(d) { return d.users; })]);

            var svg = d3.select("#" + scope.chartname).transition();

            svg.select(".headline")
              .duration(750)
              .text(function(d) {
                return Math.floor(data[data.length - 1].users); // TODO Not needed when we get real data?
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

        // Update the chart with new data from server
        var timer = $interval(updateChart, 3000);
        scope.$on('$destroy', function() {
          if (timer) {
            $interval.cancel(timer);
          }
        });
      });
    }
  }
}]);
