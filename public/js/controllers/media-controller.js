'use strict';

app.controller('MediaCtrl', function ($scope, MediaService, SubtitleService, SubtitleSrtUploadService, $routeParams, $location) {

  $scope.mediaId = parseInt($routeParams.mediaId);

  $scope.refresh = function () {
    MediaService.get({id: $scope.mediaId}, function (media) {
      $scope.media = media;
      $scope.fileUrl = media.mediaUrl;
    });
  };

  $scope.update = function () {
    MediaService.update({id: $scope.mediaId}, $scope.media, function () {
      $scope.refresh();
    });
  };

  $scope.delete = function () {
    $scope.media.$delete(function () {
      $location.path('/');
    });
  };

  $scope.refreshSubtitles = function () {
    SubtitleService.query({mediaId: $scope.mediaId}, function (subtitles) {
      $scope.subtitles = subtitles;
    });
  };

  $scope.editSubtitle = function (id) {
    SubtitleService.get({mediaId: $scope.mediaId, subtitleId: id}, function (subtitle) {
      $scope.editedSubtitle = subtitle;
    });
  };

  $scope.cancelEditing = function () {
    $scope.editedSubtitle = null;
  };

  $scope.updateSubtitle = function (id) {
    SubtitleService.update({mediaId: $scope.mediaId, subtitleId: id}, $scope.editedSubtitle, function () {
      $scope.editedSubtitle = null;
      $scope.refreshSubtitles();
    });
  };

  $scope.initializeNewSubtitle = function () {
    $scope.newSubtitle = new SubtitleService();
    $scope.newSubtitle.mediaId = $scope.mediaId;
  };

  $scope.createSubtitle = function () {
    if (!$scope.newSubtitle.offset) {
      $scope.newSubtitle.offset = $scope.player.timeCallback;
    }
    $scope.newSubtitle.$save({mediaId: $scope.mediaId}, function () {
      $scope.refreshSubtitles();
      $scope.initializeNewSubtitle();
    });
  };

  $scope.deleteSubtitle = function (subtitleId) {
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
    jumpTo: 0,
    singleSubtitleMode: true
  };

  $scope.play = function (subtitle) {
    $scope.player.jumpTo = subtitle.offset;
    $scope.currentSubtitle = subtitle;
    $scope.player.isPlaying = true;
  };

  $scope.playFromTime = function (time) {
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
      if ($scope.player.singleSubtitleMode) {
        $scope.pause();
        $scope.player.jumpTo = $scope.currentSubtitle.offset;
      } else {
        $scope.currentSubtitle = foundSub;
      }
    }
  });

  $scope.pause = function () {
    $scope.player.isPlaying = false;
  };

  $scope.isThisSubPlaying = function (sub) {
    if ($scope.currentSubtitle && sub.id == $scope.currentSubtitle.id && $scope.player.isPlaying) {
      return true;
    }
    return false;
  };

  $scope.updateTime = function (subtitle) { // updates time of subtitle by clicking it
    SubtitleService.get({mediaId: $scope.mediaId, subtitleId: subtitle.id}, function (subtitle) {
      subtitle.offset = $scope.player.timeCallback;
      SubtitleService.update({mediaId: $scope.mediaId, subtitleId: subtitle.id}, subtitle, function () {
        $scope.refreshSubtitles();
      });
    });
  };

  $scope.add = function () {
    var f = document.getElementById('srtFile').files[0],
      r = new FileReader();
    r.onloadend = function (e) {
      var data = e.target.result;
      var newSrt = new SubtitleSrtUploadService();
      newSrt.mediaId = $scope.mediaId;
      newSrt.srt = data;
      newSrt.$save({mediaId: $scope.mediaId}, function () {
        $scope.refreshSubtitles();
      });
    };
    r.readAsBinaryString(f);
  };

  $scope.selection = function(subtitle, selected) {

  };
});