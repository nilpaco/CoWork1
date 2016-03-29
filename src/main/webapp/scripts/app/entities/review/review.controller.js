'use strict';

angular.module('project1App')
    .controller('ReviewController', function ($scope, $state, Review, ReviewSearch) {

        $scope.reviews = [];
        $scope.loadAll = function() {
            Review.query(function(result) {
               $scope.reviews = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            ReviewSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.reviews = result;
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
            $scope.review = {
                text: null,
                time: null,
                id: null
            };
        };
    });
