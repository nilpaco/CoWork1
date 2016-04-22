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

        $scope.load2 = function () {
            Conversation.getMessagesFromConversation({id: $stateParams.id}, function(result) {
                $scope.messages = result;
            });
        };
        $scope.load2();

        $scope.sendMessage = function(){
            Message.postMessageFromConversation({id: $stateParams.id}, {text: $scope.text2}, function (result) {
                $scope.messages.push(result);
                $scope.text2 = null;
            });
        }


    });
