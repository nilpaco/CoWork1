'use strict';

angular.module('project1App')
    .controller('FavoriteDetailController', function ($scope, $rootScope, $stateParams, entity, Favorite, Space, User) {
        $scope.favorite = entity;
        $scope.load = function (id) {
            Favorite.get({id: id}, function(result) {
                $scope.favorite = result;
            });
        };
        var unsubscribe = $rootScope.$on('project1App:favoriteUpdate', function(event, result) {
            $scope.favorite = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
