'use strict';

app.controller('MediaCtrl', function ($scope, MediaService, SubtitleService, $routeParams, $location) {

  $scope.mediaId = parseInt($routeParams.mediaId);

  $scope.refresh = function() {
    MediaService.get({id: $scope.mediaId}, function (media) {
      $scope.media = media;
      $scope.fileUrl = media.mediaUrl;
    });
  };

  $scope.update = function() {
    MediaService.update({id: $scope.mediaId}, $scope.media, function() {
      $scope.refresh();
    });
  };

  $scope.delete = function() {
    $scope.media.$delete(function () {
      $location.path('/');
    });
  };

  $scope.refreshSubtitles = function() {
    SubtitleService.query({mediaId: $scope.mediaId}, function(subtitles) {
      $scope.subtitles = subtitles;
    });
  };

  $scope.editSubtitle = function(id) {
    SubtitleService.get({mediaId: $scope.mediaId, subtitleId: id}, function(subtitle) {
      $scope.editedSubtitle = subtitle;
    });
  };

  $scope.cancelEditing = function() {
    $scope.editedSubtitle = null;
  };

  $scope.updateSubtitle = function(id) {
    SubtitleService.update({mediaId: $scope.mediaId, subtitleId: id}, $scope.editedSubtitle, function () {
      $scope.editedSubtitle = null;
      $scope.refreshSubtitles();
    });
  };

  $scope.initializeNewSubtitle = function() {
    $scope.newSubtitle = new SubtitleService();
    $scope.newSubtitle.mediaId = $scope.mediaId;
  };

  $scope.createSubtitle = function() {
    $scope.newSubtitle.$save({mediaId: $scope.mediaId}, function() {
      $scope.refreshSubtitles();
      $scope.initializeNewSubtitle();
    });
  };

  $scope.deleteSubtitle = function(subtitleId) {
    SubtitleService.get({mediaId: $scope.mediaId, subtitleId: subtitleId}, function (subtitle) {
      subtitle.$delete({mediaId: $scope.mediaId, subtitleId: subtitleId}, function () {
        $scope.refreshSubtitles();
      });
    });
  };

  $scope.initializeNewSubtitle();
  $scope.refresh();
  $scope.refreshSubtitles();

  $scope.player = {
    isPlaying: false,
    timeCallback: 0,
    jumpTo: 0
  };

  $scope.play = function(subtitle) {
    $scope.player.jumpTo = subtitle.offset;
    $scope.player.isPlaying = true;
    $scope.currentSubtitle = subtitle;
  };

  $scope.playFromTime = function(time) {
    $scope.player.jumpTo = time;
    $scope.player.isPlaying = true;
  };

  $scope.$watch('player.timeCallback', function (currentTime) {
    var foundSub = _.find($scope.subtitles, function (sub, index) {
      if (index == $scope.subtitles.length - 1 && sub.offset <= currentTime) {
        return sub;
      } else if (sub.offset <= currentTime && $scope.subtitles[index + 1].offset > currentTime) {
        return sub;
      }
    });
    if (foundSub && (!$scope.currentSubtitle || foundSub.id != $scope.currentSubtitle.id)) {
      $scope.currentSubtitle = foundSub;
    }
  });

  $scope.pause = function() {
    $scope.player.isPlaying = false;
  };

  $scope.isThisSubPlaying = function(sub) {
    if ($scope.currentSubtitle && sub.id == $scope.currentSubtitle.id && $scope.player.isPlaying) {
      return true;
    }
    return false;
  };
});