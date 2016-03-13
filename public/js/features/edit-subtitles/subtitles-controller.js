'use strict';

app.controller('SubtitleCtrl', function ($scope, $log, $uibModal, MediaService, SubtitleService, SubtitleSrtUploadService,
                                         LanguageService, TranslationService, YesNoModalService, AddSubtitlesService,
                                         TranslationModalService, $routeParams, $location, KEY_CODES) {

  var data = {
    isPlaying: false,
    timeCallback: 0,
    jumpTo: 0,
    singleSubtitleMode: false,
    followSubtitle: false,
    mediaId: parseInt($routeParams.mediaId),
    media: null,
    subtitles: [],
    languages: LanguageService.query(),
    currentSubtitle: null,
    newSubtitle: new SubtitleService()
  };
  $scope.data = data;

  var setModel = function(media) {
    data.media = media;
  };
  var setSubtitles = function(subtitles) {
    data.subtitles = subtitles;
  };

  var showTranslation = function() {
    TranslationModalService.showDialog($scope.translations);
  };

  var refresh = function () {
    MediaService.get({id: data.mediaId}, function (media) {
      setModel(media);

      LanguageService.get({id: media.languageId}, function (language) {
        data.language = language;
      });
    });
  };

  var refreshSubtitles = function () {
    SubtitleService.query({mediaId: data.mediaId}, function (subtitles) {
      setSubtitles(subtitles);
      if (!data.currentSubtitle) {
        data.currentSubtitle = subtitles[0];
      } else {
        data.currentSubtitle = _.find(data.subtitles, function(elt) {
          if (elt.id == data.currentSubtitle.id) {
            return true;
          }
        });
      }
    });
  };

  var initializeNewSubtitle = function () {
    data.newSubtitle = new SubtitleService();
    data.newSubtitle.mediaId = data.mediaId;
  };

  $scope.selection = function(subtitle, selected, event) {
    if (event.altKey) {
      TranslationService.query({from: 'french', to: 'english', word: selected.toLowerCase().replace(/ /g , "-")}, function (translations) {
        $scope.translations = translations;
        showTranslation();
      });
    }
  };

  //////////////// functions for UI
  $scope.addSubtitlesModal = function() {
    AddSubtitlesService.showDialog(function(subtitles) {
      AddSubtitlesService.saveMultilineSubtitles(data.mediaId, subtitles, function() {
        refreshSubtitles();
      });
    });
  };

  $scope.update = function () {
    MediaService.update({id: data.mediaId}, data.media, function () {
      refresh();
    });
  };

  $scope.delete = function () {
    YesNoModalService.showDialog("Are you sure you want to delete this media?", function () {
      data.media.$delete(function () {
        $location.path('/');
      });
    });
  };

  $scope.editSubtitle = function (id) {
    SubtitleService.get({mediaId: data.mediaId, subtitleId: id}, function (subtitle) {
      $scope.editedSubtitle = subtitle;
    });
  };

  $scope.add = function () {
    var uploadSubtitles = new SubtitleSrtUploadService();
    uploadSubtitles.mediaId = data.mediaId;
    uploadSubtitles.srt = data.fileSelected;
    uploadSubtitles.$save({mediaId: data.mediaId}, function () {
      refreshSubtitles();
    });
  };

  $scope.play = function (subtitle) {
    data.jumpTo = subtitle.offset;
    data.currentSubtitle = subtitle;
    data.isPlaying = true;
  };

  $scope.pause = function () {
    data.isPlaying = false;
  };

  $scope.cancelEditing = function () {
    $scope.editedSubtitle = null;
  };

  $scope.updateSubtitle = function (id) {
    SubtitleService.update({mediaId: data.mediaId, subtitleId: id}, $scope.editedSubtitle, function () {
      $scope.editedSubtitle = null;
      refreshSubtitles();
    });
  };

  $scope.createSubtitle = function () {
    if (!data.newSubtitle.offset) {
      data.newSubtitle.offset = data.timeCallback;
    }
    data.newSubtitle.$save({mediaId: data.mediaId}, function () {
      refreshSubtitles();
      initializeNewSubtitle();
    });
  };

  $scope.deleteSubtitle = function (subtitleId) {
    YesNoModalService.showDialog("Are you sure you want to delete subtitle?", function () {
      SubtitleService.get({mediaId: data.mediaId, subtitleId: subtitleId}, function (subtitle) {
        subtitle.$delete({mediaId: data.mediaId, subtitleId: subtitleId}, function () {
          refreshSubtitles();
        });
      });
    });
  };

  $scope.playFromTime = function (time) {
    data.jumpTo = time;
    data.isPlaying = true;
  };

  $scope.isPlaying = function (sub) {
    if (data.currentSubtitle && sub.id == data.currentSubtitle.id && data.isPlaying) {
      return true;
    }
    return false;
  };

  $scope.updateTime = function (subtitle, newOffset) { // updates time of subtitle by clicking it
    if (newOffset < 0) {
      newOffset = 0;
    }
    if (newOffset == subtitle.offset) {
      return;
    }
    SubtitleService.get({mediaId: data.mediaId, subtitleId: subtitle.id}, function (subtitle) {
      subtitle.offset = newOffset;
      SubtitleService.update({mediaId: data.mediaId, subtitleId: subtitle.id}, subtitle, function () {
        refreshSubtitles();
      });
    });
  };

  $scope.$watch('player.timeCallback', function (currentTime) {
    if (!data.followSubtitle) {
      return;
    }
    var foundSub = _.find(data.subtitles, function (sub, index) {
      if (index == data.subtitles.length - 1 && sub.offset <= currentTime) {
        return sub;
      } else if (sub.offset <= currentTime && data.subtitles[index + 1].offset > currentTime) {
        return sub;
      }
    });
    if (foundSub && (!data.currentSubtitle || foundSub.id != data.currentSubtitle.id)) {
      if (data.singleSubtitleMode) {
        $scope.pause();
        data.jumpTo = data.currentSubtitle.offset;
      } else {
        data.currentSubtitle = foundSub;
      }
    }
  });

  $scope.$on('keydown', function (o, event) {
    $scope.$apply(function() {
      var offset;
      switch (event.which) {
        case KEY_CODES.MINUS:
          offset = data.currentSubtitle.offset - (event.shiftKey ? 0.1 : 1);
        case KEY_CODES.PLUS:
          offset = offset || (data.currentSubtitle.offset + (event.shiftKey ? 0.1 : 1));
        case KEY_CODES.SLASH:
          offset = offset || data.timeCallback;
          $scope.updateTime(data.currentSubtitle, offset);
          break;
        case KEY_CODES.SPACE:
          event.preventDefault();
          if (data.isPlaying) { $scope.pause(); } else { $scope.play(data.currentSubtitle); }
          break;
        case KEY_CODES.ARROW_UP:
          event.preventDefault();
          goToPreviousSubtitle(data);
          break;
        case KEY_CODES.ARROW_DOWN:
          event.preventDefault();
          goToNextSubtitleDown(data);
          break;
      }
    });
  });
  //////////////////

  initializeNewSubtitle();
  refresh();
  refreshSubtitles();

});


function goToNextSubtitleDown(data) {
  _.find(data.subtitles, function (elt, idx) {
    if (elt.id == data.currentSubtitle.id && idx == data.subtitles.length - 1) {
      return true;
    } else if (elt.id == data.currentSubtitle.id) {
      data.currentSubtitle = data.subtitles[idx + 1];
      return true;
    }
  });
}
function goToPreviousSubtitle(data) {
  _.find(data.subtitles, function (elt, idx) {
    if (elt.id == data.currentSubtitle.id && idx == 0) {
      return true;
    } else if (elt.id == data.currentSubtitle.id) {
      data.currentSubtitle = data.subtitles[idx - 1];
      return true;
    }
  });
}
