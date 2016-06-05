'use strict';

angular.module('project1App')
    .controller('SpaceController', function ($scope, $state, Space, SpaceSearch, ParseLinks, Review) {

        $scope.spaces = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.loadAll = function() {
            Space.query({page: $scope.page - 1, size: 40, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.spaces = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.search = function () {
            SpaceSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.spaces = result;
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
            $scope.space = {
                name: null,
                description: null,
                price: null,
                personMax: null,
                streetAddress: null,
                lat: null,
                lng: null,
                id: null
            };
        };

        $scope.reviews = [];
        $scope.loadAll = function() {
            Review.query(function(result) {
                $scope.reviews = result;
            });
        };
        $scope.loadAll();

        $scope.reviewsBySpace = [];
        $scope.loadAllBySpace = function() {
            Review.reviewBySpace(function(result) {
                $scope.reviewsBySpace = result;
            });
        };
        $scope.loadAllBySpace();

        $scope.onClickMarker = function (review){
            $scope.selectedReview = review;
        };


    });
