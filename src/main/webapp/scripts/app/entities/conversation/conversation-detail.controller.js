'use strict';

angular.module('project1App')
    .controller('ConversationDetailController', function ($scope, $rootScope, $stateParams, entity, Conversation, Message, Space, User) {
        $scope.messages = entity;
        $scope.load = function (id) {
            Conversation.get({id: id}, function(result) {
                $scope.conversation = result;
            });
        };
        var unsubscribe = $rootScope.$on('conversationApp:conversationUpdate', function(event, result) {
            $scope.conversation = result;
        });
        $scope.$on('$destroy', unsubscribe);

        $scope.load = function (id) {
            Conversation.getMessagesFromConversation({id: id}, function(result) {
                $scope.messages = result;
            });
        };
        $scope.sendMessage = function(){
            Conversation.postMessageFromConversation({id: $stateParams.id}, {text: $scope.text2});
        }

    });
