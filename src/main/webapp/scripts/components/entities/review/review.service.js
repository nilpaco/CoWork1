'use strict';

angular.module('project1App')
    .factory('Review', function ($resource, DateUtils) {
        return $resource('api/reviews/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.time = DateUtils.convertDateTimeFromServer(data.time);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'reviewBySpace': {
                method: 'GET',
                isArray: true,
                url: 'api/reviewsbyspace'
            },
            'reviewsByCurrentSpace': {
                method: 'GET',
                isArray: true,
                url: 'api/space/:id/reviews'
            },
            'addReview': {
                method: 'POST', isArray: false, url: 'api/space/:id/reviews'
            }



        });
    });
