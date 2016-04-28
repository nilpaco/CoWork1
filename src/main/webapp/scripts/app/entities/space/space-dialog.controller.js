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


            $scope.uploadOK = false;
            $scope.uploadStart = false;

            $scope.uploadSong = function (files) {
                $scope.filesUpload = files;
                if (files != null) {
                    for (var k = 0; k < files.length; k++) {
                        uploadUsingUpload(files[k]);
                    }
                }
            };

            $scope.save = function () {
                $scope.isSaving = true;
                $scope.song.date_posted = new Date();
                if ($scope.song.id != null) {
                    Song.update($scope.song, onSaveSuccess, onSaveError);
                } else {
                    console.log($scope.picFile);
                    if($scope.picFile == undefined){
                        //$scope.uploadArt($scope.artworkFile);
                        $scope.song.artwork = $rootScope.account.user_image;
                    }else{
                        var imageBase64 = $scope.croppedArtwork;
                        var blob = dataURItoBlob(imageBase64);
                        var file = new File([blob],"ds.jpg");
                        //$scope.uploadArt($scope.artworkFile);

                        $scope.uploadArt(file);
                    }


                    $scope.song.date_posted = new Date();
                    Song.save($scope.song, onSaveSuccess, onSaveError);
                }
            };

            function dataURItoBlob(dataURI) {
                var binary = atob(dataURI.split(',')[1]);
                var array = [];
                for(var i = 0; i < binary.length; i++) {
                    array.push(binary.charCodeAt(i));
                }
                return new Blob([new Uint8Array(array)], {type: 'image/jpeg'});
            }

            $scope.uploadArt = function (file) {
                $scope.formUpload = true;
                if (file != null) {
                    uploadUsingUploadArtwork(file)
                }
            };

            function uploadUsingUploadArtwork(file) {
                var songArtworkName = file.name.toLowerCase();
                var ext = file.name.split('.').pop();

                function randomString(length, chars) {
                    var result = '';
                    for (var i = length; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
                    return result;
                }

                var rString = "artwork-"+randomString(15, '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ') + "." + ext;
                $scope.song.artwork = "uploads/" + rString;

                Upload.upload({
                    url: 'api/upload',
                    data: {file: file, name: rString}
                }).then(function (resp) {

                    console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
                    $scope.song.artwork = "uploads/" + rString;
                }, function (resp) {
                    console.log('Error status: ' + resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
                });


            };
        }]);
