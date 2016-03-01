'use strict';

app.controller('MainCtrl', function ($scope, MediaService, SubtitleService) {

  $scope.medias = MediaService.query();
  $scope.media = new MediaService();

  $scope.addNew = function() {
    $scope.media.$save(function() {
      $scope.medias = MediaService.query();
    });
  };

  $scope.delete = function(id) {
    MediaService.get({id: id}, function (media) {
      media.$delete(function() {
        $scope.medias = MediaService.query();
      });
    });
  };

});