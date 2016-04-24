'use strict';

app.factory('SubtitleService', function ($resource) {
  return $resource('/subtitles/:subtitleId', {subtitleId: '@subtitleId'},
    {
      update: {method: 'PUT'},
      all: {
        method: 'GET',
        isArray: true,
        url: '/medias/:mediaId/subtitles',
        params: { mediaId: '@mediaId'}
      },
      create: {
        method: 'POST',
        url: '/medias/:mediaId/subtitles',
        params: { mediaId: '@mediaId'}
      },
      uploadSubtitle: {
        method: 'POST',
        url: '/medias/:mediaId/subtitles/srt',
        params: { mediaId: '@mediaId'}
      }
    }
  );
});
