'use strict';

angular.module('project1App')
    .controller('ReviewDetailController', function ($scope, $rootScope, $stateParams, entity, Review, Space, User) {
        $scope.review = entity;
        $scope.load = function (id) {
            Review.get({id: id}, function(result) {
                $scope.review = result;
            });
        };
        var unsubscribe = $rootScope.$on('project1App:reviewUpdate', function(event, result) {
            $scope.review = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
