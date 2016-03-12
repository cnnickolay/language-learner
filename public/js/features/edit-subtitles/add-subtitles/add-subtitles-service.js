'use strict';

app.factory('AddSubtitlesService', function ($uibModal) {
  return {
    showDialog: function() {
      var modalInstance = $uibModal.open({
        animation: false,
        templateUrl: '/assets/js/features/edit-subtitles/add-subtitles/add-subtitles.html',
        controller: 'AddSubtitlesCtrl',
        size: 'lg'
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
    }
  }
});