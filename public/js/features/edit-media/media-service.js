'use strict';

app.factory('MediaService', function ($resource) {
  return $resource('/medias/:mediaId', {mediaId: '@mediaId'},
    {
      update: {method: 'PUT'},
      all: {
        method: 'GET',
        url: 'mediaGroup/:mediaGroupId/medias',
        params: {mediaGroupId: '@mediaGroupId'},
        isArray: true
      },
      create: {
        method: 'POST',
        url: 'mediaGroup/:mediaGroupId/medias',
        params: {mediaGroupId: '@mediaGroupId'}
      }
    });
});