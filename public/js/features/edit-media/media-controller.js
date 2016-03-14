'use strict';

app.controller('MediaCtrl', function ($scope, $routeParams, MediaService) {

  var data = {
    mediaGroupId: parseInt($routeParams.mediaGroupId),
    medias: [],
    newMedia: new MediaService()
  };
  $scope.data = data;

  var refreshMedias = function() {
    MediaService.query(function(medias) {
      data.medias = medias;
    });
  };

  $scope.addNew = function() {
    data.newMedia.$save(function() {
      refreshMedias();
      data.newMedia = new MediaService();
    });
  };

  $scope.delete = function(id) {
    MediaService.get({id: id}, function (media) {
      media.$delete(function() {
        refreshMedias();
      });
    });
  };

});