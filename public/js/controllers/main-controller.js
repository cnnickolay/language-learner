'use strict';

app.controller('MainCtrl', function ($scope, MediaService, SubtitleService) {

  $scope.medias = MediaService.get({id: 1}, function() {

    console.log(JSON.stringify($scope.medias, null, 2));
/*
    $scope.subtitles = SubtitleService.query(function () {

    });
*/

  });

});