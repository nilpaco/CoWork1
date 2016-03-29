'use strict';

angular.module('project1App')
    .factory('SpaceSearch', function ($resource) {
        return $resource('api/_search/spaces/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
