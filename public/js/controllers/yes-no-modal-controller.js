'use strict';

app.controller('YesNoModalCtrl', function ($scope, $uibModalInstance) {

  $scope.yes = function() {
    $uibModalInstance.close(true);
  };

  $scope.no = function() {
    $uibModalInstance.dismiss(false);
  };

});