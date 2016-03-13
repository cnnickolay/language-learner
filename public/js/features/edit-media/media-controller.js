'use strict';

app.controller('MediaCtrl', function ($scope, MediaService, LanguageService) {

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

  $scope.play = function(media) {
    $scope.player.fileUrl = media.mediaUrl;
    $scope.player.isPlaying = false;
    $scope.player.isPlaying = true;
  };
  $scope.pause = function(media) {
    $scope.player.isPlaying = false;
  };

  $scope.augmentMedia = function(media) {
    LanguageService.getOne({id: media.languageId}, function(language) {
      media.language = language;
    });
  };

  $scope.languages = LanguageService.query();

});