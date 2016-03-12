'use strict';

app.controller('YesNoModalCtrl', function ($scope, $uibModalInstance, question) {

  $scope.question = question;

  $scope.yes = function() {
    $uibModalInstance.close(true);
  };

  $scope.no = function() {
    $uibModalInstance.dismiss(false);
  };

});