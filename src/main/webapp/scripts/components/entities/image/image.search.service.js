'use strict';

angular.module('project1App')
    .factory('ImageSearch', function ($resource) {
        return $resource('api/_search/images/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
