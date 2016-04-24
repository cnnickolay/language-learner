'use strict';

app.controller('MediaGroupCtrl', function ($scope, MediaGroupService, LanguageService, YesNoModalService) {

  var data = {
    mediaGroups: [],
    newMediaGroup: {}
  };
  $scope.data = data;

  ///////////////

  var refreshMediaGroups = function () {
    MediaGroupService.query(function (mediaGroups) {
      data.mediaGroups = mediaGroups;
    });
  };

  var instantiateNewMediaGroup = function () {
    data.newMediaGroup = new MediaGroupService();
  };

  var refreshLanguages = function () {
    LanguageService.query(function (languages) {
      data.languages = languages;
    });
  };

  $scope.augmentLanguage = function (mediaGroup) {
    LanguageService.getOne({id: mediaGroup.languageId}, function (language) {
      mediaGroup.language = language;
    });
  };

  $scope.create = function () {
    data.newMediaGroup.$save(function () {
      refreshMediaGroups();
    });
  };

  $scope.delete = function (mediaGroup) {
    YesNoModalService.showDialog("Are you sure you want to delete " + mediaGroup.name + "?", function() {
      MediaGroupService.delete({mediaGroupId: mediaGroup.id}, function() {
        refreshMediaGroups();
      });
    });
  };

  ///////////////
  refreshLanguages();
  refreshMediaGroups();
  instantiateNewMediaGroup();

});