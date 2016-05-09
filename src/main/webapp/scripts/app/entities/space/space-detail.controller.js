'use strict';

angular.module('project1App')
    .controller('SpaceDetailController', function ($scope, $rootScope, $stateParams, entity, Space, Service, Image, Favorite, Review, Conversation, User, Message, $timeout) {
        $scope.space = entity;
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
                $scope.reviewsByCurrentSpace = result;
            });
        };

        $scope.loadAllBySpace($stateParams.id);

        $scope.addMessage = function (){
            Message.addMessage({id:$stateParams.id}, {text:$scope.text2});
        }

        $scope.addReview = function (){
            Review.addReview({id:$stateParams.id}, {text:$scope.text3},function(result){
                $scope.reviewsByCurrentSpace.push(result);
            });
        }

        $scope.imageGallery2 =[];
        $scope.loadAllImagesBySpace = function(id) {
            Image.imagesByCurrentSpace({id: id}, function(result) {
                $scope.imageGallery2 = result;
            });
        };
        $scope.loadAllImagesBySpace($stateParams.id);

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
            for (var i = 0; i < $scope.imageGallery2.length; i++) {
                $scope.imageGallery[i] = {thumb: 'uploads/'+$scope.imageGallery2[i].image, img: 'uploads/'+$scope.imageGallery2[i].image, description: $scope.space.name};
            }
        };
        $timeout(function (){
            $scope.loadImages()
        },100);


    });
