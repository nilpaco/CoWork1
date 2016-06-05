'use strict';

angular.module('project1App')
    .controller('MainController', function ($scope, Principal, ParseLinks, Space, SpaceSearch, Favorite, toaster, NgMap, Service, filterFilter) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        $scope.spaces = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.spacesByFilters = [];
        $scope.loadAll = function() {
            Space.getUserSpaces({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
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
        $scope.searchSpace   = '';

        $scope.search = function () {
            SpaceSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.spaces = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.sendFilters = function(firstPrice, lastPrice, numPers, ids, address){
            if(ids!=[]){
                Array.prototype.clean = function(deleteValue) {
                    for (var i = 0; i < this.length; i++) {
                        if (this[i] == deleteValue) {
                            this.splice(i, 1);
                            i--;
                        }
                    }
                    return this;
                };
                ids.clean(null);

                var servicesStr = ids.join("-");

            }


            if(address==""){
                address = null;
            }
            if(firstPrice==null && lastPrice==null && numPers==null && ids.length==0 && address==null){
                $scope.loadAll();
            }
            Space.byFilters({minprice: firstPrice, maxprice: lastPrice, numpers: numPers, services: servicesStr, address: address}, function (result) {
                $scope.spaces = result;
            });
            $scope.ids = [];

        }

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

        $scope.like = function(id){
            Favorite.addLike({id: id},{},successLike);
        }

        var successLike = function(result) {
            for (var k = 0; k < $scope.spaces.length; k++) {
                if ($scope.spaces[k].space.id == result.space.id) {
                    $scope.spaces[k].liked = result.liked;
                }
            }
            if(result.liked == false){
                toaster.pop('success',result.space.name,"Removed");

            }else{
                toaster.pop('success',result.space.name,"Added")
            }
        }

        var vm = this;
        NgMap.getMap().then(function(map) {
            vm.map = map;
        });

        $scope.onClickMarker = function (space){
            $scope.selectedSpace = space;
        };

        $scope.loadAll2 = function() {
            Service.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.services = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll2();
        };
        $scope.loadAll2();

        $scope.ids = [];

        $scope.clear = function () {
            $scope.firstPrice = null;
            $scope.lastPrice = null;
            $scope.ids = [];
            $scope.numPers = null;
            $scope.address = null;
            $scope.loadAll();

        }

    });
