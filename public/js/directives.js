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
    link: function(scope, element, attributes) {
      element.on('mouseup', function (e) {
        var selected = window.getSelection().toString();
        if (selected) {
          scope.selection({selected: selected});
          //scope.$apply();
        }
      });
    }
  };
});

app.directive('keypressEvents', [
  '$document',
  '$rootScope',
  '$window',
  function($document, $rootScope, $window) {
    return {
      restrict: 'A',
      link: function() {
        $window.addEventListener("keydown", function(e) {
          if([32, 37, 38, 39, 40].indexOf(e.keyCode) > -1) {
            e.preventDefault();
          }
          $rootScope.$broadcast('keydown', e);
          $rootScope.$broadcast('keydown:' + e.which, e);
        }, false);
      }
    };
  }
]);