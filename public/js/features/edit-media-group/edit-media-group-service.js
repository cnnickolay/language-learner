'use strict';

app.factory('MediaGroupService', function ($resource) {
  return {
    api: $resource('/mediaGroups/:id', {id: '@id'}, {
      'update': {method: 'PUT'}
    })
  }
});