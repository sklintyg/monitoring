angular.module('MonitorControllers')
.controller('AlternativeMonitorController', ['$scope', '$http', '$location', '$interval',
  function($scope, $http, $location, $interval) {

    var timer = $interval(function() {
      var iframe = document.getElementById('pingdomframe');
      iframe.src = iframe.src;
    }, 30000);

    // Set the maximum amount of failed retries
    $scope.threshold = 10;
    // Set the time between retiess
    var retryTime = 30000;
    // Counts the number of retries

    $scope.webcert = {
      doneLoading: false,
      fail: 0,
      name: 'webcert'
    };
    $scope.minaintyg = {
      doneLoading: false,
      fail: 0,
      name: 'minaintyg'
    };
    $scope.privatlakarportalen = {
      doneLoading: false,
      fail: 0,
      name: 'privatlakarportalen'
    };

    $scope.webcert.timer = $interval(function() {
      checkIfDone($scope.webcert)
    }, retryTime);
    $scope.minaintyg.timer = $interval(function() {
      checkIfDone($scope.minaintyg)
    }, retryTime);
    $scope.privatlakarportalen.timer = $interval(function() {
      checkIfDone($scope.privatlakarportalen)
    }, retryTime);

    checkIfDone($scope.webcert);
    checkIfDone($scope.minaintyg);
    checkIfDone($scope.privatlakarportalen);

    function checkIfDone(service) {
      $http.get('/api/counters/' + service.name)
        .success(function(data) {
          if (data.length != 0) {
            service.doneLoading = true;
            $interval.cancel(service.timer);
          }
          else {
            service.fail++;
            if (service.fail > $scope.threshold) {
              $interval.cancel(service.timer);
            }
          }
        })
      .error(function() {
        service.fail++;
        if (service.fail > $scope.threshold) {
          $interval.cancel(service.timer);
        }
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
      if ($scope.privatlakarportalen.timer) {
        $interval.cancel($scope.privatlakarportalen.timer);
      }
    });

}])
