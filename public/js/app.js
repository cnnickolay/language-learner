'use strict';

var app = angular.module('lang', ['ngResource', 'ngRoute'])
  .config(function($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/templates/index.html',
        controller: 'MainCtrl'
      })
      .when('/media/:mediaId', {
        templateUrl: '/assets/templates/media.html',
        controller: 'MediaCtrl'
      });
  });

