'use strict';

angular.module('project1App')
    .controller('ImageController', function ($scope, $state, Image, ImageSearch, ParseLinks, $timeout) {

        $scope.images = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.imageGallery =[];
        $scope.loadAll = function() {
            Image.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.images = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.search = function () {
            ImageSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.images = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.image = {
                image: null,
                id: null
            };
        };
        var self = this;

        self.images2 = [
            {thumb: 'http://placehold.it/350x150', img: 'http://placehold.it/350x150', description: 'Image 1'},
            {thumb: 'http://placehold.it/350x150', img: 'http://placehold.it/350x150', description: 'Image 2'},
            {thumb: 'http://placehold.it/350x150', img: 'http://placehold.it/350x150', description: 'Image 3'},
            {thumb: 'http://placehold.it/350x150', img: 'http://placehold.it/350x150', description: 'Image 4'}
        ];

        $scope.loadImages = function () {
            for (var i = 0; i < $scope.images.length; i++) {
                $scope.imageGallery[i] = {thumb: 'uploads/'+$scope.images[i].image+'.jpg', img: 'uploads/'+$scope.images[i].image+'.jpg', description: $scope.images[i].space.name};
            }
        };
        $timeout(function (){
            $scope.loadImages()
        },100);


    });
