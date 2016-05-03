'use strict';

angular.module('project1App').controller('SpaceDialogController',
    ['$scope', '$stateParams', 'entity', 'Space', 'Service', 'Image', 'Favorite', 'Review', 'Conversation', 'User', 'NgMap', '$state', 'Upload',
        function($scope, $stateParams, entity, Space, Service, Image, Favorite, Review, Conversation, User, NgMap, $state, Upload) {

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
            //$uibModalInstance.close(result);
            $state.go('space', null, {reload: true});
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.space.lat = $scope.lat;
            $scope.space.lng = $scope.lng;
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
        NgMap.getMap({id: 'foomap'}).then(function(map) {
            vm.map = map;
        });


        $scope.lat = [];
        $scope.lng = [];
        vm.placeChanged = function() {
            vm.place = this.getPlace();
            vm.map.setCenter(vm.place.geometry.location);
            $scope.lat = vm.place.geometry.location.lat();
            $scope.lng = vm.place.geometry.location.lng();
        }


            $scope.imageStrings = [];
            $scope.processFiles = function(files){
                angular.forEach(files, function(flowFile, i){
                    var fileReader = new FileReader();
                    fileReader.onload = function (event) {
                        var uri = event.target.result;
                        $scope.imageStrings[i] = uri;
                    };
                    fileReader.readAsDataURL(flowFile.file);
                });
            };

            $scope.submit = function() {
                    $scope.upload($scope.file);
            };

            $scope.upload = function (file) {
                Upload.upload({
                    url: 'api/upload',
                    data: {file: file, 'name': 'hola'}
                }).then(function (resp) {
                    console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
                }, function (resp) {
                    console.log('Error status: ' + resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
                });
            };




        }]);
