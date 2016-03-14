'use strict';

var app = angular.module('lang', ['ngResource', 'ngRoute', 'ui.bootstrap'])
  .config(function($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/js/features/edit-media-group/edit-media-group.html',
        controller: 'MediaGroupCtrl'
      })
      .when('/mediaGroup/:mediaGroupId?', {
        templateUrl: '/assets/js/features/edit-media/media.html',
        controller: 'MediaCtrl'
      })
      .when('/mediaGroup/:mediaGroupId/media/:mediaId', {
        templateUrl: '/assets/js/features/edit-subtitles/subtitles.html',
        controller: 'SubtitleCtrl',
        reloadOnSearch: false
      });
  });

app.constant("KEY_CODES", {
  ARROW_UP: 38,
  ARROW_DOWN: 40,
  MINUS: 189,
  PLUS: 187,
  SPACE: 32,
  SLASH: 191
});