'use strict';

angular.module('project1App')
    .controller('SpaceController', function ($scope, $state, Space, SpaceSearch, ParseLinks, NgMap) {

        $scope.spaces = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.loadAll = function() {
            Space.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
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
        var vm = this;
        NgMap.getMap({id: 'foomap'}).then(function(map) {
            vm.map = map;
            console.log('NgMap.getMap in SpaceController', map);
        });
        $scope.lat = [];
        $scope.lng = [];
        vm.placeChanged = function() {
            vm.place = this.getPlace();
            console.log('location', vm.place.geometry.location);
            vm.map.setCenter(vm.place.geometry.location);
            $scope.lat = vm.place.geometry.location.lat();
            $scope.lng = vm.place.geometry.location.lng();

        }



    });
