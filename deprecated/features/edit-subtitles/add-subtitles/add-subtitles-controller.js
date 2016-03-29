'use strict';

app.controller('AddSubtitlesCtrl', function ($scope, $uibModalInstance) {

  $scope.add = function() {
    var arrayOfLines = $scope.subtitles.match(/[^\r\n]+/g);
    $uibModalInstance.close(arrayOfLines);
  };

  $scope.cancel = function() {
    $uibModalInstance.dismiss('cancel');
  };

});