'use strict';

app.controller('MediaCtrl', function ($scope, MediaService, SubtitleService, $routeParams) {

  $scope.mediaId = $routeParams.mediaId;

  MediaService.get({id: $scope.mediaId}, function (media) {
    $scope.media = media;
  });

});