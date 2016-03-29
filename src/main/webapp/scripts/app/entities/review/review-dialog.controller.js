'use strict';

angular.module('project1App').controller('ReviewDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Review', 'Space', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Review, Space, User) {

        $scope.review = entity;
        $scope.spaces = Space.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Review.get({id : id}, function(result) {
                $scope.review = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('project1App:reviewUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.review.id != null) {
                Review.update($scope.review, onSaveSuccess, onSaveError);
            } else {
                Review.save($scope.review, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForTime = {};

        $scope.datePickerForTime.status = {
            opened: false
        };

        $scope.datePickerForTimeOpen = function($event) {
            $scope.datePickerForTime.status.opened = true;
        };
}]);
