'use strict';

angular.module('project1App')
	.controller('ConversationDeleteController', function($scope, $uibModalInstance, entity, Conversation) {

        $scope.conversation = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Conversation.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
