'use strict';

app.factory('LanguageService', function ($resource) {
  return $resource('/languages/:id', {id: '@id'},
    {
      getOne: {
        method: 'GET',
        cache: true
      }
    }
  );
});