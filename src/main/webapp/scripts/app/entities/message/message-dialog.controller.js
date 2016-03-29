'use strict';

angular.module('project1App').controller('MessageDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Message', 'Conversation', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Message, Conversation, User) {

        $scope.message = entity;
        $scope.conversations = Conversation.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Message.get({id : id}, function(result) {
                $scope.message = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('project1App:messageUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.message.id != null) {
                Message.update($scope.message, onSaveSuccess, onSaveError);
            } else {
                Message.save($scope.message, onSaveSuccess, onSaveError);
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
