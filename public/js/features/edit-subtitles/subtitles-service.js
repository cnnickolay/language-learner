'use strict';

app.factory('SubtitleService', function ($resource) {
  return $resource('/medias/:mediaId/subtitles/:subtitleId', {mediaId: '@mediaId', subtitleId: '@subtitleId'},
    {'update': {method: 'PUT'}}
  );
});

app.factory('SubtitleSrtUploadService', function ($resource) {
  return $resource('/medias/:mediaId/subtitles/srt', {mediaId: '@mediaId'});
});