'use strict';

app.factory('MediaGroupService', function ($resource) {
  return $resource('/mediaGroups/:mediaGroupId', {id: '@mediaGroupId'}, {
    'update': {method: 'PUT'}
  })
});