'use strict';

angular.module('project1App')
    .controller('SpaceDetailController', function ($scope, $rootScope, $stateParams, entity, Space, Service, Image, Favorite, Review, Conversation, User) {
        $scope.space = entity;
        $scope.load = function (id) {
            Space.get({id: id}, function(result) {
                $scope.space = result;
            });
        };
        var unsubscribe = $rootScope.$on('project1App:spaceUpdate', function(event, result) {
            $scope.space = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
