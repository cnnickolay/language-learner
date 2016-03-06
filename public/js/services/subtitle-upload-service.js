'use strict';

app.factory('SubtitleSrtUploadService', function ($resource) {
  return $resource('/medias/:mediaId/subtitles/srt', {mediaId: '@mediaId', subtitleId: '@subtitleId'});
});