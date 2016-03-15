'use strict';

app.factory('SubtitleService', function ($resource) {
  return $resource('/subtitles/:subtitleId', {subtitleId: '@subtitleId'},
    {
      update: {method: 'PUT'},
      all: {
        method: 'GET',
        isArray: true,
        url: '/media/:mediaId/subtitles',
        params: { mediaId: '@mediaId'}
      },
      create: {
        method: 'POST',
        url: '/media/:mediaId/subtitles',
        params: { mediaId: '@mediaId'}
      },
      uploadSubtitle: {
        method: 'POST',
        url: '/media/:mediaId/subtitles/srt',
        params: { mediaId: '@mediaId'}
      }
    }
  );
});
