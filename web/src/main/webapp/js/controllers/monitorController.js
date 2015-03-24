angular.module('MonitorControllers', [])
.controller('AlternativeMonitorController', ['$scope', '$http', '$location', '$interval',
    function($scope, $http, $location, $interval) {

      var timer = $interval(function() {
        var iframe = document.getElementById('pingdomframe');
        iframe.src = iframe.src;
      }, 30000);

      $scope.$on('$destroy', function() {
        if (timer) {
          $interval.cancel(timer);
        }
      });

    }])
.controller('MonitorController', ['$scope', '$http', '$location',
    function($scope, $http, $location) {
    }]);
