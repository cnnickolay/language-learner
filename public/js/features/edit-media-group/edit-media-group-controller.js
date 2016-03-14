'use strict';

app.controller('MediaGroupCtrl', function ($scope, MediaGroupService) {

  var data = {
    mediaGroups: []
  };
  $scope.data = data;

  ///////////////

  var refreshMediaGroups = function() {
    MediaGroupService.api.query(function(mediaGroups) {
      data.mediaGroups = mediaGroups;
    });
  };

  ///////////////
  refreshMediaGroups();

});