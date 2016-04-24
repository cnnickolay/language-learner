'use strict';

app.factory('AddSubtitlesService', function ($uibModal, SubtitleService) {
  return {
    showDialog: function(callback) {
      var modalInstance = $uibModal.open({
        animation: false,
        templateUrl: '/assets/js/features/edit-subtitles/add-subtitles/add-subtitles.html',
        controller: 'AddSubtitlesCtrl',
        size: 'lg'
      });

      modalInstance.result.then(function (subtitles) {
        callback(subtitles);
      });
    },

    saveMultilineSubtitles: function(mediaId, subtitles, storedCallback) {
      var inProgress = 0;
      var idx = 0;
      _.each(subtitles, function (subtitle) {
        var Subtitle = new SubtitleService();
        Subtitle.text = subtitle;
        Subtitle.mediaId = mediaId;
        Subtitle.offset = idx;
        idx += 0.5;
        inProgress++;
        Subtitle.$save({mediaId: mediaId}, function() {
          inProgress--;
          if (!inProgress) {
            storedCallback();
          }
        });
      });
    }
  }
});