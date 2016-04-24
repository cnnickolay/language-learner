'use strict';

app.filter('filterSubtitleByName', function() {
  return function(items, position, offset, search) {
    if (!search && !offset && !position) {
      return items;
    }
    var result = [];
    var fromPosition, toPosition;

    if (position) {
      if (position.indexOf('-') > -1) {
        var range = position.split('-');
        fromPosition = parseInt(range[0]);
        toPosition = parseInt(range[1]);
      } else {
        fromPosition = parseInt(position);
        toPosition = parseInt(position);
      }
    }

    _.forEach(items, function (subtitle, idx) {
      var filtered = true;
      if (subtitle && subtitle.text.indexOf(search) < 0) {
        filtered = false;
      }
      if (fromPosition && (fromPosition > idx || idx > toPosition)) {
        filtered = false;
      }
      if (filtered) {
        result.push(subtitle);
      }
    });
    return result;
  };
});