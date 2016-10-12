angular.module('MonitorControllers')
.controller('MonitorController', ['$scope', '$http', '$location', '$interval',
  function($scope, $http, $location, $interval) {
    // Set the maximum amount of failed retries
    $scope.threshold = 10;
    // Set the time between retries
    var retryTime = 30000;

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
    $scope.rehabstod = {
      doneLoading: false,
      fail: 0,
      name: 'rehabstod'
    };
    $scope.intygstjanst = {
      doneLoading: false,
      fail: 0,
      name: 'intygstjanst'
    };
    $scope.privatlakarportal = {
      doneLoading: false,
      fail: 0,
      name: 'privatlakarportal'
    };
    $scope.statistik = {
      doneLoading: false,
      fail: 0,
      name: 'statistik'
    };


    $scope.webcert.timer = $interval(function() {
      checkIfDone($scope.webcert)
    }, retryTime);
    $scope.minaintyg.timer = $interval(function() {
      checkIfDone($scope.minaintyg)
    }, retryTime);
    $scope.rehabstod.timer = $interval(function() {
      checkIfDone($scope.rehabstod)
    }, retryTime);
    $scope.intygstjanst.timer = $interval(function() {
      checkIfDone($scope.intygstjanst)
    }, retryTime);
    $scope.privatlakarportal.timer = $interval(function() {
      checkIfDone($scope.privatlakarportal)
    }, retryTime);
    $scope.statistik.timer = $interval(function() {
      checkIfDone($scope.statistik)
    }, retryTime);

    checkIfDone($scope.webcert);
    checkIfDone($scope.minaintyg);
    checkIfDone($scope.rehabstod);
    checkIfDone($scope.intygstjanst);
    checkIfDone($scope.privatlakarportal);
    checkIfDone($scope.statistik);

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
      if ($scope.rehabstod.timer) {
        $interval.cancel($scope.rehabstod.timer);
      }
      if ($scope.intygstjanst.timer) {
        $interval.cancel($scope.intygstjanst.timer);
      }
      if ($scope.privatlakarportal.timer) {
        $interval.cancel($scope.privatlakarportal.timer);
      }
      if ($scope.statistik.timer) {
        $interval.cancel($scope.statistik.timer);
      }
    });
}]);
