'use strict';

angular.module('project1App')
    .factory('Message', function ($resource, DateUtils) {
        return $resource('api/messages/:id', {}, {
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
            'addMessage': {method: 'POST', isArray: false, url: 'api/space/:id/message'},
            'postMessageFromConversation': {method: 'POST', isArray: false, url: 'api/conversation/:id/message'}
        });
    });
