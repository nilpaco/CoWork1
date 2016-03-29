'use strict';

angular.module('project1App')
    .factory('Favorite', function ($resource, DateUtils) {
        return $resource('api/favorites/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'addLike': { method: 'POST', isArray: false, url: 'api/favorites/:id/like'}
        });
    });
