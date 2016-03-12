'use strict';

var app = angular.module('lang', ['ngResource', 'ngRoute', 'ui.bootstrap'])
  .config(function($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/js/features/edit-media/media.html',
        controller: 'MediaCtrl'
      })
      .when('/media/:mediaId', {
        templateUrl: '/assets/js/features/edit-subtitles/subtitles.html',
        controller: 'SubtitleCtrl'
      });
  });

