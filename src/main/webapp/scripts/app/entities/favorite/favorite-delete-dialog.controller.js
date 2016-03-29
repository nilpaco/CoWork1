'use strict';

angular.module('project1App')
	.controller('FavoriteDeleteController', function($scope, $uibModalInstance, entity, Favorite) {

        $scope.favorite = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Favorite.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
