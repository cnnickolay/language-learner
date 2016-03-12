'use strict';

app.factory('TranslationService', function ($resource) {
  return $resource('/translations?from=:from&to=:to&word=:word', {from: '@from', to: '@to', word: '@word'});
});

app.factory('TranslationModalService', function ($uibModal) {
  return {
    showDialog: function(translations) {
      $uibModal.open({
        animation: false,
        templateUrl: '/assets/js/features/translation/translation.html',
        controller: 'TranslatorCtrl',
        size: 'lg',
        resolve: {
          translations: function () {
            return translations;
          }
        }
      });
    }
  }
});