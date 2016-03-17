'use strict';

app.controller('MediaCtrl', function ($scope, $routeParams, MediaService, MediaGroupService) {

  var data = {
    mediaGroupId: parseInt($routeParams.mediaGroupId),
    medias: [],
    newMedia: {}
  };
  $scope.data = data;

  var refreshMedias = function() {
    if (data.mediaGroupId) {
      MediaService.allByMediaGroupId({mediaGroupId: data.mediaGroupId}, function(medias) {
        data.medias = medias;
      });
    } else {
      MediaService.all(function(medias) {
        data.medias = medias;
      });
    }
  };

  var instantiateNewMedia = function() {
    var mediaService = new MediaService();
    if (data.mediaGroupId) {
      mediaService.mediaGroupId = data.mediaGroupId;
    }
    data.newMedia = mediaService;
  };

  $scope.addNew = function() {
    data.newMedia.$save(function() {
      refreshMedias();
      instantiateNewMedia();
    });
  };

  $scope.delete = function(mediaId) {
    MediaService.delete({mediaId: mediaId}, function() {
      refreshMedias();
    });
  };

  $scope.augmentMedia = function(media) {
    if (!media.mediaGroupId) return;
    MediaGroupService.get({mediaGroupId: media.mediaGroupId}, function (mediaGroup) {
      media.mediaGroup = mediaGroup;
    });
  };

  refreshMedias();
  instantiateNewMedia();
});