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

      scope.statusList = [];
      function fetchStatus() {
        $http.get('/api/status/' + scope.servicename)
          .success(function(data, status, headers, config) {
            scope.statusList = data;
          })
          .error(function(data, status, headers, config) {
            console.log('Could not reach server for the status for service ' + servicename);
          });
      };
      fetchStatus();

      var timer = $interval(fetchStatus, 30000);
      scope.$on('$destroy', function() {
        if (timer) {
          $interval.cancel(timer);
        }
      });
    }
  }
}]);
