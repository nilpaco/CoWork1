'use strict';

angular.module('project1App').controller('ConversationDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Conversation', 'Message', 'Space', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Conversation, Message, Space, User) {

        $scope.conversation = entity;
        $scope.messages = Message.query();
        $scope.spaces = Space.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Conversation.get({id : id}, function(result) {
                $scope.conversation = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('project1App:conversationUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.conversation.id != null) {
                Conversation.update($scope.conversation, onSaveSuccess, onSaveError);
            } else {
                Conversation.save($scope.conversation, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
