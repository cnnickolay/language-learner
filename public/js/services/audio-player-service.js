'use strict';

app.factory('AudioPlayerService', function () {
  return {
    isPlaying: false,
    fileUrl: "",
    timeCallback: 0,
    jumpTo: 0
  }
});