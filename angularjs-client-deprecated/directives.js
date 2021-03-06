'use strict';

app.directive('audioPlayer', function () {
  return {
    restrict: 'E',
    scope: {
      isPlaying: "=",
      fileUrl: "@",
      timeCallback: "=",
      jumpTo: "="
    },
    replace: true,
    template: "<audio player> <p>Your browser does not support the <code>audio</code> element.</p> </audio>",
    link: function (scope, element, attrs) {
      scope.$watch('fileUrl', function (fileUrl) {
        element.removeAttr('src');
        element.attr('src', fileUrl);
        if (scope.isPlaying) {
          element[0].play();
        }
      });

      scope.$watch('isPlaying', function (isPlaying) {
        if (isPlaying) {
          element[0].play();
        } else {
          element[0].pause();
        }
      });

      scope.$watch('jumpTo', function (time) {
        if (time != -1) {
          element[0].currentTime = time;
          scope.jumpTo = -1;
        }
      });

      element.on('timeupdate', function () {
        scope.timeCallback = Math.round(this.currentTime * 10) / 10;
        scope.$apply();
      });
      element.on('pause', function () {
        scope.isPlaying = false;
        scope.$apply();
      });
      element.on('play', function () {
        scope.isPlaying = true;
        scope.$apply();
      });
    }
  };
});


app.directive('selector', function () {
  return {
    restrict: 'A',
    scope: {
      selection: '&'
    },
    link: function (scope, element, attributes) {
      element.on('mouseup', function (e) {
        var selected = window.getSelection().toString();
        if (selected) {
          scope.selection({selected: selected, event: e});
        }
      });
    }
  };
});

app.directive('keypressEvents', [
  '$document',
  '$rootScope',
  '$window',
  function ($document, $rootScope, $window) {
    return {
      restrict: 'A',
      scope: {
        keysToIgnore: '='
      },
      link: function (scope) {
        $window.addEventListener("keydown", function (e) {
          $rootScope.$broadcast('keydown', e);
          $rootScope.$broadcast('keydown:' + e.which, e);
        }, false);
      }
    };
  }
]);

app.directive('readFile', function () {
  return {
    restrict: 'A',
    scope: {
      fileSelected: '='
    },
    link: function (scope, element) {
      $(element[0]).change(function () {
        var file = element[0].files[0];
        var reader = new FileReader();
        reader.onloadend = function (e) {
          scope.$apply(function () {
            scope.fileSelected = e.target.result;
          });
        };
        reader.readAsBinaryString(file);
      });
    }
  }
});

app.directive('autoScroll', function () {
  return {
    restrict: 'A',
    scope: {
      elementId: '@'
    },
    link: function (scope) {
      scope.$watch('elementId', function (elementId) {
        var element = $('#' + elementId);
        if (!element || !element.offset()) {
          return;
        }
        var offset = element.offset().top;
        var visible_area_start = $(window).scrollTop();
        var visible_area_end = visible_area_start + window.innerHeight;

        if (offset < visible_area_start || offset > visible_area_end) {
          // Not in view so scroll to it
          $('html,body').animate({scrollTop: offset - window.innerHeight / 3}, 1000);
        }
      });
    }
  };
});