'use strict';

angular.module('project1App')
    .factory('ReviewSearch', function ($resource) {
        return $resource('api/_search/reviews/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
