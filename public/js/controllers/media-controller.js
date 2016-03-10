'use strict';

app.controller('MediaCtrl', function ($scope, $log, $uibModal, MediaService, SubtitleService, SubtitleSrtUploadService, LanguageService, TranslationService, $routeParams, $location) {

  $scope.mediaId = parseInt($routeParams.mediaId);

  $scope.showTranslation = function () {
    var modalInstance = $uibModal.open({
      animation: false,
      templateUrl: '/assets/templates/word-translation.html',
      controller: 'TranslatorCtrl',
      size: 'lg',
      resolve: {
        translations: function () {
          return $scope.translations;
        }
      }
    });
  };

  $scope.addSubtitlesModal = function() {
    var modalInstance = $uibModal.open({
      animation: false,
      templateUrl: '/assets/templates/add-subtitles.html',
      controller: 'AddSubtitlesCtrl',
      size: 'lg',
      resolve: {}
    });

    modalInstance.result.then(function (subtitles) {
      var inProgress = 0;
      var idx = 0;
      _.each(subtitles, function (subtitle) {
        var Subtitle = new SubtitleService();
        Subtitle.text = subtitle;
        Subtitle.mediaId = $scope.mediaId;
        Subtitle.offset = idx;
        idx += 0.5;
        inProgress++;
        Subtitle.$save({mediaId: $scope.mediaId}, function() {
          inProgress--;
          if (!inProgress) {
            $scope.refreshSubtitles();
          }
        });
      });
    });
  };

  $scope.refresh = function () {
    MediaService.get({id: $scope.mediaId}, function (media) {
      $scope.media = media;
      $scope.fileUrl = media.mediaUrl;

      LanguageService.get({id: media.languageId}, function (language) {
        $scope.language = language;
      });
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
      if (!$scope.currentSubtitle)
        $scope.currentSubtitle = subtitles[5];
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
    singleSubtitleMode: false
  };
  $scope.followSubtitle = false;

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
    if (!$scope.followSubtitle) {
      return;
    }
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
    TranslationService.query({from: 'french', to: 'english', word: selected.toLowerCase().replace(/ /g , "-")}, function (translations) {
      $scope.translations = translations;
      $scope.showTranslation();
      console.log(JSON.stringify(translations, null, 2));
    });
  };

  $scope.languages = LanguageService.query();

  $scope.$on('keydown:40', function() {
    _.find($scope.subtitles, function (elt, idx) {
      if (elt.id == $scope.currentSubtitle.id && idx == $scope.subtitles.length - 1) {
        return true;
      } else if (elt.id == $scope.currentSubtitle.id) {
        $scope.$apply(function () {
          $scope.currentSubtitle = $scope.subtitles[idx + 1];
        });
        return true;
      }
    });
  });
  $scope.$on('keydown:38', function() {
    _.find($scope.subtitles, function (elt, idx) {
      if (elt.id == $scope.currentSubtitle.id && idx == 0) {
        return true;
      } else if (elt.id == $scope.currentSubtitle.id) {
        $scope.$apply(function () {
          $scope.currentSubtitle = $scope.subtitles[idx - 1];
        });
        return true;
      }
    });
  });
  $scope.$on('keydown:32', function () {
    $scope.$apply(function() {
      if ($scope.player.isPlaying) {
        $scope.pause();
      } else {
        $scope.play($scope.currentSubtitle);
      }
    });
  });
  $scope.$on('keydown:191', function () {
    $scope.$apply(function() {
      $scope.updateTime($scope.currentSubtitle);
    });
  });
});