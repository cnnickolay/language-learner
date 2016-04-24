'use strict';

app.factory('MediaService', function ($resource) {
  return $resource('/medias/:mediaId', {mediaId: '@mediaId'},
    {
      update: {method: 'PUT'},
      all: {
        method: 'GET',
        url: '/medias',
        isArray: true
      },
      allByMediaGroupId: {
        method: 'GET',
        url: 'mediaGroups/:mediaGroupId/medias',
        params: {mediaGroupId: '@mediaGroupId'},
        isArray: true
      }
    });
});