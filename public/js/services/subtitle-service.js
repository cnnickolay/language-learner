'use strict';

app.factory('SubtitleService', function ($resource) {
  return $resource('/medias/:id/subtitles', {id: '@id'});
});