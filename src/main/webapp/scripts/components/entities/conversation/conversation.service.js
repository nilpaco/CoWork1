'use strict';

angular.module('project1App')
    .factory('Conversation', function ($resource, DateUtils) {
        return $resource('api/conversations/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'getMessagesFromConversation': {method: 'GET', isArray: true, url: 'api/conversations/:id/messages'},
            'postMessageFromConversation': {method: 'POST', isArray: false, url: 'api/conversation/:id/messages'},
            'getConversationFromSpace': {method: 'GET', isArray: false, url: 'api/space/:id/conversations'},
        });
    });
