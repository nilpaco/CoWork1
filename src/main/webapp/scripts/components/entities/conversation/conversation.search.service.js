'use strict';

angular.module('project1App')
    .factory('ConversationSearch', function ($resource) {
        return $resource('api/_search/conversations/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
