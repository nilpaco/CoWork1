'use strict';

angular.module('project1App')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


