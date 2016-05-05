'use strict';

angular.module('project1App')
    .factory('Image', function ($resource, DateUtils) {
        return $resource('api/images/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'imagesByCurrentSpace': {
                method: 'GET',
                isArray: true,
                url: 'api/space/:id/images'
            }
        });
    });
