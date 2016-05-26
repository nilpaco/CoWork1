'use strict';

angular.module('project1App')
    .controller('SpaceDetailController', function ($scope, $rootScope, $stateParams, imageSpace, entity, Space, Service, Image, Favorite, Review, Conversation, User, Message, $timeout) {
        $scope.space = entity;
        $scope.test = false;
        $scope.testReview = false;
        $scope.testConv = false;
        $scope.imageSpace = imageSpace;
        $scope.imageGallery =[];
        $scope.load = function (id) {
            Space.get({id: id}, function(result) {
                $scope.space = result;
            });
        };
        var unsubscribe = $rootScope.$on('project1App:spaceUpdate', function(event, result) {
            $scope.space = result;
        });
        $scope.$on('$destroy', unsubscribe);

        $scope.reviewsByCurrentSpace = [];
        $scope.loadAllBySpace = function(id) {
            Review.reviewsByCurrentSpace({id: id}, function(result) {
                for(var i=0; i<result.length;i++){
                    if($scope.account.login == result[0].user.login){
                        $scope.testReview = true;
                    }
                }
                $scope.reviewsByCurrentSpace = result;
            });
        };

        $scope.loadAllBySpace($stateParams.id);

        $scope.addMessage = function (){
            Message.addMessage({id:$stateParams.id}, {text:$scope.text2});
            $scope.testConv = true;
        }

        $scope.addReview = function (){
            Review.addReview({id:$stateParams.id}, {text:$scope.text3},function(result){
                $scope.reviewsByCurrentSpace.push(result);
                $scope.test = true;
            });
        }

        $scope.loadConversation = function(id) {
            Conversation.getConversationFromSpace({id: id}, function(result) {
                $scope.getConversation = result;
            });
        };
        $scope.loadConversation($stateParams.id);

        $scope.onClickMarker = function (review){
            $scope.selectedReview = review;
        };

        $scope.loadImages = function () {
            for (var i = 0; i < $scope.imageSpace.length; i++) {
                $scope.imageGallery[i] = {thumb: 'uploads/'+$scope.imageSpace[i].image, img: 'uploads/'+$scope.imageSpace[i].image, description: $scope.space.name};
            }
        };
        $timeout(function (){
            $scope.loadImages()
        },100);


    });
