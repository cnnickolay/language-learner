'use strict';

app.factory('TranslationService', function ($resource) {
  return $resource('/translations?from=:from&to=:to&word=:word', {from: '@from', to: '@to', word: '@word'});
});