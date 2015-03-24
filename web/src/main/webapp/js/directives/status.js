angular.module('MonitorDirectives')
.directive('monitorservices', ['d3Service', '$interval', '$http', function(d3Service, $interval, $http) {
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
      function fetchStatus() {
        $http.get('/api/status/' + scope.servicename)
          .success(function(data, status, headers, config) {
            console.log('service success');
            console.log(data);
            generateStatus();
          })
          .error(function(data, status, headers, config) {
            console.log('service failure');
            generateStatus();
          });
      };
      fetchStatus();

      var timer = $interval(fetchStatus, 3000);
      scope.$on('$destroy', function() {
        if (timer) {
          $interval.cancel(timer);
        }
      });
    }
  }
}]);
