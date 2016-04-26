'use strict';

angular.module('project1App').controller('ImageDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Image', 'Space',
        function($scope, $stateParams, $uibModalInstance, entity, Image, Space) {

        $scope.image = entity;
        $scope.spaces = Space.query();
        $scope.load = function(id) {
            Image.get({id : id}, function(result) {
                $scope.image = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('project1App:imageUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.image.id != null) {
                Image.update($scope.image, onSaveSuccess, onSaveError);
            } else {
                Image.save($scope.image, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
