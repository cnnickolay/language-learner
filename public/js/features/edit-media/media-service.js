'use strict';

app.factory('MediaService', function ($resource) {
  return $resource('/medias/:id', {id: '@id'}, {
    'update': {method: 'PUT'}
  });
});