'use strict';

angular.module('project1App')
    .factory('Space', function ($resource, DateUtils) {
        return $resource('api/spaces/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'getUserSpaces': {
                method: 'GET',
                isArray: true,
                url: 'api/spaces/userliked'
            },
            'byFilters': {
                method: 'GET',
                isArray: true,
                url: 'api/spaces/byfilters'
            },


        });
    });
