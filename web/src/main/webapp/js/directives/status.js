angular.module('MonitorDirectives')
.directive('monitorservices', ['d3Service', '$interval', function(d3Service, $interval) {
  return {
    restrict: 'E',
    scope: {
      servicename: '@',
      alertsize: '@'
    },
    transclude: true,
    templateUrl: 'partials/services.html',
    link: function(scope, element, attrs) {
      function generateStatus() {
        scope.statusList =  [
          {
            "service": "HSA",
            "status": (Math.random() < 0.5 ? "FAIL" : "OK"),
            "severity": 0
          },
          {
            "service": "ActiveMQ",
            "status": (Math.random() < 0.5 ? "FAIL" : "OK"),
            "severity": 0
          },
          {
            "service": "Databas",
            "status": (Math.random() < 0.5 ? "FAIL" : "OK"),
            "severity": 0
          },
          {
            "service": "Intygstjänsten",
            "status": (Math.random() < 0.5 ? "FAIL" : "OK"),
            "severity": 0
          },
          {
            "service": "Skickade Intyg",
            "status": Math.floor(Math.random() * 1000),
            "severity": 2
          }];
        scope.statusList.map(function(d) {
          if (d.service !== "Skickade Intyg")
            d.severity = (d.status == "OK" ? 0 : 1);
        });
      }
      generateStatus();

      var timer = $interval(function() {
        generateStatus();
      }, 3000);
      scope.$on('$destroy', function() {
        if (timer) {
          $interval.cancel(timer);
        }
      });
    }
  }
}]);
