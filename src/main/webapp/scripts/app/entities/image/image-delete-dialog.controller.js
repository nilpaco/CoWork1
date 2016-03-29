'use strict';

angular.module('project1App')
	.controller('ImageDeleteController', function($scope, $uibModalInstance, entity, Image) {

        $scope.image = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Image.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
