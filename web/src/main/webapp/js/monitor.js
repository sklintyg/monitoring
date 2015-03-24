angular.module('MonitorDirectives', ['d3']);
angular.module('MonitorControllers', []);
var monitor = angular.module('monitor', ['ngRoute', 'MonitorControllers', 'MonitorDirectives']);

monitor.config(['$routeProvider', function($routeProvider) {
  $routeProvider
    .when('/', {
      templateUrl: 'partials/standard.html',
      controller: 'MonitorController'
    })
  .when('/alternative', {
    templateUrl: 'partials/alternative.html',
    controller: 'MonitorController'
  }).
  otherwise({
    redirectTo: '/'
  });
}]);
