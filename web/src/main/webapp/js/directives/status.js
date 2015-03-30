angular.module('MonitorDirectives')
.directive('monitorservices', ['d3Service', '$interval', '$http', '$compile', function(d3Service, $interval, $http, $compile) {
  return {
    restrict: 'E',
    scope: {
      servicename: '@',
      alertsize: '@'
    },
    transclude: true,
    templateUrl: 'partials/services.html',
    link: function(scope, element, attrs) {

      scope.statusList = [];
      function fetchStatus() {
        $http.get('/api/status/' + scope.servicename)
          .success(function(data, status, headers, config) {
            for (var i = 0; i < data.length; i++) {
              if (data[i].servicename == "version") {
                var version = data[i].statuscode.split(";");
                scope.serviceversion = version[0];
                scope.checktime = version[1];
                data.splice(i, 1);
              }
            }
            scope.statusList = data;
          })
          .error(function(data, status, headers, config) {
            console.log('Could not reach server for the status for service ' + scope.servicename);
          });
      };
      fetchStatus();

      var timer = $interval(fetchStatus, 5000);
      scope.$on('$destroy', function() {
        if (timer) {
          $interval.cancel(timer);
        }
      });
    }
  }
}]);
