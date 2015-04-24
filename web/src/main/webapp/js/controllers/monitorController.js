angular.module('MonitorControllers')
.controller('MonitorController', ['$scope', '$http', '$location', '$interval',
  function($scope, $http, $location, $interval) {
    $scope.webcert = {
      doneLoading: false,
      name: 'webcert'
    };
    $scope.minaintyg = {
      doneLoading: false,
      name: 'minaintyg'
    };
    $scope.statistik = {
      doneLoading: false,
      name: 'statistik'
    };

    $scope.webcert.timer = $interval(function() {
      checkIfDone($scope.webcert)
    }, 1000);
    $scope.minaintyg.timer = $interval(function() {
      checkIfDone($scope.minaintyg)
    }, 1000);
    $scope.statistik.timer = $interval(function() {
      checkIfDone($scope.statistik)
    }, 1000);

    function checkIfDone(service) {
      $http.get('/api/counters/' + service.name)
        .success(function(data) {
          if (data.length != 0) {
            service.doneLoading = true;
            $interval.cancel(service.timer);
          }
        })
      .error(function() {
      });
    }

    $scope.$on('$destroy', function() {
      if (timer) {
        $interval.cancel(timer);
      }
      if ($scope.webcert.timer) {
        $interval.cancel($scope.webcert.timer);
      }
      if ($scope.minaintyg.timer) {
        $interval.cancel($scope.minaintyg.timer);
      }
      if ($scope.statistik.timer) {
        $interval.cancel($scope.statistik.timer);
      }
    });
}]);
