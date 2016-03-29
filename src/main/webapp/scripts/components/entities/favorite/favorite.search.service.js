'use strict';

angular.module('project1App')
    .factory('FavoriteSearch', function ($resource) {
        return $resource('api/_search/favorites/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
