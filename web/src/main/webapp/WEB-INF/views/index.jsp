<%@ page contentType="text/html; charset=UTF-8" %>
<html ng-app="monitor">
  <head>
    <meta charset="utf-8">
    <title>Intygstjänsten monitorering</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/monitor.css" type="text/css">
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.13/angular.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.13/angular-route.min.js"></script>
    <script src="js/monitor.js"></script>
    <script src="js/controllers/monitorController.js"></script>
    <script src="js/controllers/alternativeMonitorController.js"></script>
    <script src="js/directives/chart.js"></script>
    <script src="js/directives/status.js"></script>
    <script src="js/d3module.js"></script>
  </head>
  <body>
    <div ng-view>
    </div>
  </body>
</html>
