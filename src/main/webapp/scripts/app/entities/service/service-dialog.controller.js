'use strict';

angular.module('project1App').controller('ServiceDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Service', 'Space',
        function($scope, $stateParams, $uibModalInstance, entity, Service, Space) {

        $scope.service = entity;
        $scope.spaces = Space.query();
        $scope.load = function(id) {
            Service.get({id : id}, function(result) {
                $scope.service = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('project1App:serviceUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.service.id != null) {
                Service.update($scope.service, onSaveSuccess, onSaveError);
            } else {
                Service.save($scope.service, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
