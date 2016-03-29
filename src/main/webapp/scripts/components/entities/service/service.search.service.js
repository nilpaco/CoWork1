'use strict';

angular.module('project1App')
    .factory('ServiceSearch', function ($resource) {
        return $resource('api/_search/services/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
