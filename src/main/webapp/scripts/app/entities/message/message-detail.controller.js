'use strict';

angular.module('project1App')
    .controller('MessageDetailController', function ($scope, $rootScope, $stateParams, entity, Message, Conversation, User) {
        $scope.message = entity;
        $scope.load = function (id) {
            Message.get({id: id}, function(result) {
                $scope.message = result;
            });
        };
        var unsubscribe = $rootScope.$on('project1App:messageUpdate', function(event, result) {
            $scope.message = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
