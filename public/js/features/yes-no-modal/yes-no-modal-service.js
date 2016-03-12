'use strict';

app.factory('YesNoModalService', function ($uibModal) {
  return {
    showDialog: function (question, positiveFunction) {
      var modalInstance = $uibModal.open({
        animation: false,
        templateUrl: '/assets/js/features/yes-no-modal/yes-no-modal.html',
        controller: 'YesNoModalCtrl',
        size: 'sm',
        resolve: {
          question: function () {
            return question;
          }
        }
      });

      modalInstance.result.then(function (result) {
        if (result) {
          positiveFunction();
        }
      });
    }
  }
});