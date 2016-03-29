'use strict';

angular.module('project1App').controller('SpaceDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Space', 'Service', 'Image', 'Favorite', 'Review', 'Conversation', 'User', 'NgMap',
        function($scope, $stateParams, $uibModalInstance, entity, Space, Service, Image, Favorite, Review, Conversation, User, NgMap) {

        $scope.space = entity;
        $scope.services = Service.query();
        $scope.images = Image.query();
        $scope.favorites = Favorite.query();
        $scope.reviews = Review.query();
        $scope.conversations = Conversation.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Space.get({id : id}, function(result) {
                $scope.space = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('project1App:spaceUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.space.id != null) {
                Space.update($scope.space, onSaveSuccess, onSaveError);
            } else {
                Space.save($scope.space, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
            var vm = this;
            $scope.lat = [];
            $scope.lng = [];
            vm.placeChanged = function() {
                vm.place = this.getPlace();
                console.log('location', vm.place.geometry.location);
                vm.map.setCenter(vm.place.geometry.location);
                $scope.lat = vm.place.geometry.location.lat();
                $scope.lng = vm.place.geometry.location.lng();

            }
            NgMap.getMap().then(function(map) {
                vm.map = map;
            });

        }]);
